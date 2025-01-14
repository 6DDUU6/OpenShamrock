package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.ark.WeatherSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_weather")
internal object GetWeather: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        session.getIntOrNull("code")?.let {
            return invoke(it, session.echo)
        }
        session.getString("city").let {
            return invoke(it, session.echo)
        }
    }

    suspend operator fun invoke(code: Int, echo: JsonElement = EmptyJsonString): String {
        val result = WeatherSvc.fetchWeatherCard(code)
        if (result.isFailure) {
            return error("fetch weather failed", echo)
        }
        return ok(result.getOrThrow(), echo)
    }

    suspend operator fun invoke(city: String, echo: JsonElement = EmptyJsonString): String {
        val code = WeatherSvc.searchCity(city)
        if (code.isFailure || code.getOrThrow().isEmpty()) {
            return error("search city failed", echo)
        }
        return invoke(code.getOrThrow().first().adcode, echo)
    }
}
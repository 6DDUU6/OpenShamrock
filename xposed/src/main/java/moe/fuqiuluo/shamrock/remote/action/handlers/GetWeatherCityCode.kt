package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.ark.WeatherSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_weather_city_code")
internal object GetWeatherCityCode: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val city = session.getString("city")
        return invoke(city, session.echo)
    }

    suspend operator fun invoke(city: String, echo: JsonElement = EmptyJsonString): String {
        val result = WeatherSvc.searchCity(city)
        if (result.isFailure) {
            return error(result.exceptionOrNull()?.message ?: "unknown error", echo)
        }

        val regions = result.getOrThrow()

        return ok(regions, echo)
    }

    override val requiredParams: Array<String> = arrayOf("city")
}
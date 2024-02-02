package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.shamrock.utils.PlatformUtils
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_device_battery")
internal object GetDeviceBattery: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(session.echo)
    }

    operator fun invoke(echo: JsonElement = EmptyJsonString): String {
        return ok(PlatformUtils.getDeviceBattery(), echo = echo)
    }
}
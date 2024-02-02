package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonArray
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_guild_list")
internal object GetGuildList : IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(echo = session.echo)
    }

    operator fun invoke(echo: JsonElement = EmptyJsonString): String {
        // TODO: get_guild_list
        return ok(EmptyJsonArray, echo, "此功能尚未实现")
    }
}
package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("restart_me")
internal object RestartMe: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(2000, session.echo)
    }

    operator fun invoke(duration: Int, echo: JsonElement = EmptyJsonString): String {
        return ok("不支持", echo)
    }
}
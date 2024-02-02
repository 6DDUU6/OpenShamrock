package moe.qiufuluo.shamrock.remote.action.handlers

import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.structures.EmptyObject
import moe.qiufuluo.shamrock.remote.structures.Status
import moe.qiufuluo.shamrock.remote.structures.resultToString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_latest_events")
internal object GetLatestEvents: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return resultToString(
            true, Status.Ok, listOf<EmptyObject>(), echo = session.echo
        )
    }
}
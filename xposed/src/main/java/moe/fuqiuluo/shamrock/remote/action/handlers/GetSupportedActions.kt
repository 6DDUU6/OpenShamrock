package moe.qiufuluo.shamrock.remote.action.handlers

import moe.qiufuluo.shamrock.remote.action.ActionManager
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.structures.Status
import moe.qiufuluo.shamrock.remote.structures.resultToString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_supported_actions")
internal object GetSupportedActions: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return resultToString(true, Status.Ok, ActionManager.actionMap.keys.toList(), echo = session.echo)
    }
}
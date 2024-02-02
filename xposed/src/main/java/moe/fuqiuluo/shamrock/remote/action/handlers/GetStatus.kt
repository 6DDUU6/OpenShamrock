package moe.qiufuluo.shamrock.remote.action.handlers

import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.structures.Status
import moe.qiufuluo.shamrock.remote.structures.resultToString
import moe.qiufuluo.shamrock.remote.service.data.BotStatus
import moe.qiufuluo.shamrock.remote.service.data.Self
import moe.qiufuluo.shamrock.xposed.helper.AppRuntimeFetcher
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_status", ["status"])
internal object GetStatus: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val runtime = AppRuntimeFetcher.appRuntime
        val curUin = runtime.currentAccountUin
        return resultToString(true, Status.Ok, listOf(
            BotStatus(
                Self("qq", curUin.toLong()), runtime.isLogin, status = "正常", good = runtime.isLogin
            )
        ), echo = session.echo)
    }
}
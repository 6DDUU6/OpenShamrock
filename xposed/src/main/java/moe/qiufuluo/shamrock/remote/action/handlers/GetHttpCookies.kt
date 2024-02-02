package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.TicketSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.service.data.Credentials
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_http_cookies")
internal object GetHttpCookies : IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val appid = session.getString("appid")
        val daid = session.getString("daid")
        val jumpurl = session.getString("jumpurl")
        return invoke(appid, daid, jumpurl, session.echo)
    }

    suspend operator fun invoke(
        appid: String,
        daid: String,
        jumpurl: String,
        echo: JsonElement = EmptyJsonString
    ): String {
        val ck = TicketSvc.GetHttpCookies(appid, daid, jumpurl) ?: ""
        return ok(Credentials(cookie = ck), echo)
    }
}
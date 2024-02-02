package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.VisitorSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.shamrock.tools.errMsg
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("send_like")
internal object SendLike: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val times = session.getInt("times")
        val uin = session.getLong("user_id")
        return invoke(uin, times, session.echo)
    }

    suspend operator fun invoke(uin: Long, cnt: Int, echo: JsonElement = EmptyJsonString): String {
        val result = VisitorSvc.vote(uin, cnt)
        return if(result.isSuccess) {
            ok("成功", echo)
        } else {
            logic(result.errMsg(), echo)
        }
    }

    override val requiredParams: Array<String> = arrayOf("times", "user_id")
}
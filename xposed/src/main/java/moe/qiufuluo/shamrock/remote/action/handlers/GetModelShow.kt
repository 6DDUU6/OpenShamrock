package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.CardSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_model_show")
internal object GetModelShow: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val uin = session.getLongOrNull("user_id")
        return if (uin == null) {
            invoke(session.echo)
        } else {
            invoke(uin, session.echo)
        }
    }

    suspend operator fun invoke(echo: JsonElement = EmptyJsonString): String {
        return ok(CardSvc.getModelShow(), echo)
    }

    suspend operator fun invoke(uin: Long, echo: JsonElement = EmptyJsonString): String {
        if (uin == 0L) {
            return invoke(echo)
        }
        return ok(CardSvc.getModelShow(uin), echo)
    }
}
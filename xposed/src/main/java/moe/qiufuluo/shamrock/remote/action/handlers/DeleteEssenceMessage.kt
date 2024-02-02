package moe.qiufuluo.shamrock.remote.action.handlers

import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.qqinterface.servlet.MsgSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("delete_essence_msg", ["delete_essence_message"])
internal object DeleteEssenceMessage: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val messageId = session.getInt("message_id")
        return invoke(messageId, session.echo)
    }

    suspend operator fun invoke(messageId: Int, echo: JsonElement = EmptyJsonString): String {
        val msg = MsgSvc.getMsg(messageId).onFailure {
            return logic("Obtain msg failed, please check your msg_id.", echo)
        }.getOrThrow()
        val (success, tip) = GroupSvc.deleteEssenceMessage(
            if (msg.chatType == MsgConstant.KCHATTYPEGROUP) msg.peerUin else 0,
            msg.msgSeq,
            msg.msgRandom
        )
        return if (success) {
            ok("成功", echo)
        } else {
            logic(tip, echo)
        }
    }
}
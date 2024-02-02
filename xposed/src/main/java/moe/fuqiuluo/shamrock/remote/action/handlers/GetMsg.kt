package moe.qiufuluo.shamrock.remote.action.handlers

import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.helper.MessageHelper
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.service.data.MessageDetail
import moe.qiufuluo.shamrock.remote.service.data.MessageSender
import moe.qiufuluo.qqinterface.servlet.MsgSvc
import moe.qiufuluo.qqinterface.servlet.msg.convert.MessageConvert
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_message", ["get_msg"])
internal object GetMsg: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val hashCode = session.getIntOrNull("message_id")
            ?: session.getInt("msg_id")
        return invoke(hashCode, session.echo)
    }

    suspend operator fun invoke(msgHash: Int, echo: JsonElement = EmptyJsonString): String {
        val msg = MsgSvc.getMsg(msgHash).onFailure {
            return logic("Obtain msg failed, please check your msg_id.", echo)
        }.getOrThrow()
        val seq = msg.msgSeq.toInt()
        return ok(MessageDetail(
            time = msg.msgTime.toInt(),
            msgType = MessageHelper.obtainDetailTypeByMsgType(msg.chatType),
            msgId = msgHash,
            realId = seq,
            sender = MessageSender(
                msg.senderUin, msg.sendNickName
                    .ifEmpty { msg.sendMemberName }
                    .ifEmpty { msg.sendRemarkName }
                    .ifEmpty { msg.peerName }, "unknown", 0, msg.senderUid
            ),
            message = MessageConvert.convertMessageRecordToMsgSegment(msg).map {
                it.toJson()
            },
            peerId = msg.peerUin,
            groupId = if (msg.chatType == MsgConstant.KCHATTYPEGROUP) msg.peerUin else 0,
            targetId = if (msg.chatType != MsgConstant.KCHATTYPEGROUP) msg.peerUin else 0
        ), echo)
    }

    override val requiredParams: Array<String> = arrayOf("message_id")
}
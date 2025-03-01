package moe.qiufuluo.qqinterface.servlet

import com.tencent.mobileqq.msf.core.MsfCore
import com.tencent.qqnt.kernel.nativeinterface.Contact
import com.tencent.qqnt.kernel.nativeinterface.IKernelMsgService
import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.core.writeInt
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import moe.qiufuluo.shamrock.remote.action.handlers.GetHistoryMsg
import moe.qiufuluo.shamrock.remote.service.listener.AioListener
import moe.qiufuluo.shamrock.tools.broadcast
import moe.qiufuluo.shamrock.utils.DeflateTools
import moe.whitechi73.protobuf.message.JsonElement
import moe.whitechi73.protobuf.message.MessageBody
import moe.whitechi73.protobuf.message.MessageContentHead
import moe.whitechi73.protobuf.message.MessageElement
import moe.whitechi73.protobuf.message.MessageElementList
import moe.whitechi73.protobuf.message.MessageHead
import moe.whitechi73.protobuf.message.RichMessage
import moe.whitechi73.protobuf.push.MessagePush
import mqq.app.MobileQQ
import kotlin.coroutines.resume

internal object PacketSvc: BaseSvc() {
    /**
     * 伪造收到Json卡片消息
     */
    suspend fun fakeSelfRecvJsonMsg(msgService: IKernelMsgService, content: String): Long {
        return fakeReceiveSelfMsg(msgService) {
            listOf(MessageElement(
                    json = JsonElement((byteArrayOf(1) + DeflateTools.compress(content.toByteArray())))
            ))
        }
    }

    private suspend fun fakeReceiveSelfMsg(msgService: IKernelMsgService, builder: () -> List<MessageElement>): Long {
        val latestMsg = withTimeoutOrNull(3000) {
            suspendCancellableCoroutine {
                msgService.getMsgs(Contact(MsgConstant.KCHATTYPEC2C, app.currentUid, ""), 0L, 1, true) { code, why, msgs ->
                    it.resume(GetHistoryMsg.GetMsgResult(code, why, msgs))
                }
            }
        }?.data?.firstOrNull()
        val msgSeq = (latestMsg?.msgSeq ?: 0) + 1

        val msgPush = MessagePush(
            msgBody = MessageBody(
                msgHead = MessageHead(
                    peer = app.longAccountUin,
                    peerUid = app.currentUid,
                    flag = 1001,
                    receiver = app.longAccountUin,
                    receiverUid = app.currentUid
                ),
                contentHead = MessageContentHead(
                    msgType = 166,
                    msgSubType = 11,
                    msgSeq = msgSeq,
                    u1 = msgSeq,
                    msgTime = System.currentTimeMillis() / 1000,
                    u2 = 1,
                    u3 = msgSeq,
                    msgRandom = msgService.getMsgUniqueId(System.currentTimeMillis()),
                    u4 = msgSeq - 2,
                    u5 = msgSeq
                ),
                richMsg = RichMessage(MessageElementList(builder()))
            )
        )

        fakeReceive("trpc.msg.olpush.OlPushService.MsgPush", 10000, ProtoBuf.encodeToByteArray(msgPush))
        return withTimeoutOrNull(5000L) {
            suspendCancellableCoroutine {
                AioListener.messageLessListenerMap[msgSeq] = {
                    it.resume(this.msgId)
                }
            }
        } ?: -1L
    }

    /**
     * 伪造QQ收到某个包
     */
    private fun fakeReceive(cmd: String, seq: Int, buffer: ByteArray) {
        MobileQQ.getContext().broadcast("msf") {
            putExtra("__cmd", "fake_packet")
            putExtra("package_cmd", cmd)
            putExtra("package_uin", app.currentUin)
            putExtra("package_seq", seq)
            val wupBuffer = BytePacketBuilder().apply {
                writeInt(buffer.size + 4)
                writeFully(buffer)
            }.build()
            putExtra("package_buffer", wupBuffer.readBytes())
            wupBuffer.release()
        }
    }
}
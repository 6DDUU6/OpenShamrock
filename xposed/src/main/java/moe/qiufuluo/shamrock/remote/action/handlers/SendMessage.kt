package moe.qiufuluo.shamrock.remote.action.handlers

import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.helper.MessageHelper
import moe.qiufuluo.shamrock.helper.ParamsException
import moe.qiufuluo.qqinterface.servlet.MsgSvc
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.remote.service.data.MessageResult
import moe.qiufuluo.shamrock.tools.json
import moe.qiufuluo.shamrock.helper.Level
import moe.qiufuluo.shamrock.helper.LogCenter
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.shamrock.tools.jsonArray
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("send_msg", ["send_message"])
internal object SendMessage: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val detailType = session.getStringOrNull("detail_type") ?: session.getStringOrNull("message_type")
        try {
            val chatType = detailType?.let {
                MessageHelper.obtainMessageTypeByDetailType(it)
            } ?: run {
                if (session.has("user_id")) {
                    if (session.has("group_id")) {
                        MsgConstant.KCHATTYPETEMPC2CFROMGROUP
                    } else {
                        MsgConstant.KCHATTYPEC2C
                    }
                } else if (session.has("group_id")) {
                    MsgConstant.KCHATTYPEGROUP
                } else {
                    return noParam("detail_type/message_type", session.echo)
                }
            }
            val peerId = when(chatType) {
                MsgConstant.KCHATTYPEGROUP -> session.getStringOrNull("group_id") ?: return noParam("group_id", session.echo)
                MsgConstant.KCHATTYPEC2C, MsgConstant.KCHATTYPETEMPC2CFROMGROUP -> session.getStringOrNull("user_id") ?: return noParam("user_id", session.echo)
                else -> error("unknown chat type: $chatType")
            }
            val fromId = when(chatType) {
                MsgConstant.KCHATTYPEGROUP, MsgConstant.KCHATTYPETEMPC2CFROMGROUP -> session.getStringOrNull("group_id") ?: return noParam("group_id", session.echo)
                MsgConstant.KCHATTYPEC2C -> session.getStringOrNull("user_id") ?: return noParam("user_id", session.echo)
                else -> error("unknown chat type: $chatType")
            }
            val retryCnt = session.getIntOrNull("retry_cnt")
            val recallDuration = session.getLongOrNull("recall_duration")
            return if (session.isString("message")) {
                val autoEscape = session.getBooleanOrDefault("auto_escape", false)
                val message = session.getString("message")
                invoke(chatType, peerId, message, autoEscape, echo = session.echo, fromId = fromId, retryCnt = retryCnt ?: 3, recallDuration = recallDuration)
            } else if (session.isArray("message")) {
                val message = session.getArray("message")
                invoke(chatType, peerId, message, session.echo, fromId = fromId, retryCnt ?: 3, recallDuration = recallDuration)
            } else {
                val message = session.getObject("message")
                invoke(chatType, peerId, listOf( message ).jsonArray, session.echo, fromId = fromId, retryCnt ?: 3, recallDuration = recallDuration)
            }
        } catch (e: ParamsException) {
            return noParam(e.message!!, session.echo)
        } catch (e: Throwable) {
            return logic(e.message ?: e.toString(), session.echo)
        }
    }

    // 发送文本格式/CQ码类型消息
    suspend operator fun invoke(
        chatType: Int,
        peerId: String,
        message: String,
        autoEscape: Boolean,
        fromId: String = peerId,
        retryCnt: Int,
        recallDuration: Long?,
        echo: JsonElement = EmptyJsonString
    ): String {
        //if (!ContactHelper.checkContactAvailable(chatType, peerId)) {
        //    return logic("contact is not found", echo = echo)
        //}
        val result = if (autoEscape) {
            MsgSvc.sendToAio(chatType, peerId, listOf(
                mapOf(
                    "type" to "text",
                    "data" to mapOf(
                        "text" to message
                    )
                )
            ).json, fromId = fromId, retryCnt)
        } else {
            val msg = MessageHelper.decodeCQCode(message)
            if (msg.isEmpty()) {
                LogCenter.log("CQ码不合法", Level.WARN)
                return logic("CQCode is illegal", echo)
            } else {
                MsgSvc.sendToAio(chatType, peerId, msg, fromId = fromId, retryCnt)
            }
        }
        if (result.isFailure) {
            return logic(result.exceptionOrNull()?.message ?: "", echo)
        }
        val sendMsgResult = result.getOrThrow()
        if (sendMsgResult.msgHashId <= 0) {
            return logic("send message failed", echo = echo)
        }
        recallDuration?.let { autoRecall(sendMsgResult.msgHashId, it) }
        return ok(MessageResult(
            msgId = sendMsgResult.msgHashId,
            time = (sendMsgResult.msgTime * 0.001).toLong()
        ), echo = echo)
    }

    // 消息段格式消息
    suspend operator fun invoke(
        chatType: Int, peerId: String, message: JsonArray, echo: JsonElement = EmptyJsonString, fromId: String = peerId, retryCnt: Int, recallDuration: Long?,
    ): String {
        //if (!ContactHelper.checkContactAvailable(chatType, peerId)) {
        //    return logic("contact is not found", echo = echo)
        //}
        val result = MsgSvc.sendToAio(chatType, peerId, message, fromId = fromId, retryCnt)
        if (result.isFailure) {
            return logic(result.exceptionOrNull()?.message ?: "", echo)
        }
        val sendMsgResult = result.getOrThrow()
        if (sendMsgResult.msgHashId <= 0) {
            return logic("send message failed", echo = echo)
        }
        recallDuration?.let { autoRecall(sendMsgResult.msgHashId, it) }
        return ok(MessageResult(
            msgId = sendMsgResult.msgHashId,
            time = (sendMsgResult.msgTime * 0.001).toLong()
        ), echo)
    }

    private fun autoRecall(msgHash: Int, duration: Long) {
        GlobalScope.launch(Dispatchers.Default) {
            delay(duration)
            MsgSvc.recallMsg(msgHash)
        }
    }

    override val requiredParams: Array<String> = arrayOf("message")
}
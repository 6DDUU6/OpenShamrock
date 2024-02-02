package moe.qiufuluo.shamrock.remote.action.handlers

import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.qqinterface.servlet.MsgSvc
import moe.qiufuluo.qqinterface.servlet.TicketSvc
import moe.qiufuluo.shamrock.helper.Level
import moe.qiufuluo.shamrock.helper.LogCenter
import moe.qiufuluo.shamrock.helper.MessageHelper
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.service.HttpService
import moe.qiufuluo.shamrock.tools.asBoolean
import moe.qiufuluo.shamrock.tools.asBooleanOrNull
import moe.qiufuluo.shamrock.tools.asInt
import moe.qiufuluo.shamrock.tools.asIntOrNull
import moe.qiufuluo.shamrock.tools.asJsonObject
import moe.qiufuluo.shamrock.tools.asLong
import moe.qiufuluo.shamrock.tools.asString
import moe.qiufuluo.shamrock.tools.json
import moe.qiufuluo.shamrock.tools.jsonArray
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler(".handle_quick_operation_async")
internal object QuickOperation: IActionHandler() {
    val actionMsgTypes = arrayOf(
        "record", "voice", "video", "markdown"
    )

    override suspend fun internalHandle(session: ActionSession): String {
        val botId = session.getLong("self_id")
        if (botId != TicketSvc.getLongUin()) {
            return logic("当前登录账号和输入的`self_id`不一致", session.echo)
        }
        val context = session.getObject("context")
        //val msgType = context["message_type"].asString
        val msgHash = context["message_id"].asInt
        //val peerId = context[when(msgType) {
        //    "group" -> "group_id"
        //    "private" -> "user_id"
        //    else -> error("unknown message type: $msgType")
        //}].asLong
        val record = MsgSvc.getMsg(msgHash).getOrNull()
            ?: return logic("获取源消息失败", session.echo)

        val operation = session.getObject("operation")

        if (operation.containsKey("reply")) {
            LogCenter.log({ "websocket quickly reply successfully" }, Level.DEBUG)
            val autoEscape = operation["auto_escape"].asBooleanOrNull ?: false
            val atSender = operation["at_sender"].asBooleanOrNull ?: false
            val autoReply = operation["auto_reply"].asBooleanOrNull ?: true
            val message = operation["reply"]
            if (message is JsonPrimitive) {
                if (autoEscape) {
                    val msgList = mutableSetOf<JsonElement>()
                    msgList.add(mapOf(
                        "type" to "text",
                        "data" to mapOf(
                            "text" to message.asString
                        )
                    ).json)
                    quicklyReply(
                        record,
                        msgList.jsonArray,
                        msgHash,
                        atSender,
                        autoReply
                    )
                } else {
                    val messageArray = MessageHelper.decodeCQCode(message.asString)
                    quicklyReply(
                        record,
                        messageArray,
                        msgHash,
                        atSender,
                        autoReply
                    )
                }
            } else if (message is JsonArray) {
                quicklyReply(
                    record,
                    message,
                    msgHash,
                    atSender,
                    autoReply
                )
            }
        }

        if (MsgConstant.KCHATTYPEGROUP == record.chatType && operation.containsKey("delete") && operation["delete"].asBoolean) {
            MsgSvc.recallMsg(msgHash)
        }
        if (MsgConstant.KCHATTYPEGROUP == record.chatType && operation.containsKey("kick") && operation["kick"].asBoolean) {
            GroupSvc.kickMember(record.peerUin, false, "", record.senderUin)
        }
        if (MsgConstant.KCHATTYPEGROUP == record.chatType && operation.containsKey("ban") && operation["ban"].asBoolean) {
            val banTime = operation["ban_duration"].asIntOrNull ?: (30 * 60)
            if (banTime <= 0) return logic("禁言时间必须大于0", session.echo)
            GroupSvc.banMember(record.peerUin, record.senderUin, banTime)
        }

        return logic("操作成功", session.echo)
    }

    override val requiredParams: Array<String> = arrayOf("context", "operation", "self_id")

    suspend fun quicklyReply(
        record: MsgRecord,
        message: JsonArray,
        msgHash: Int,
        atSender: Boolean,
        autoReply: Boolean
    ) {
        val messageList = mutableListOf<JsonElement>()
        message.filter {
            it.asJsonObject["type"]?.asString in actionMsgTypes
        }.let {
            if (it.isNotEmpty()) {
                it.map { listOf(it) }.forEach {
                    MsgSvc.sendToAio(record.chatType, record.peerUin.toString(), it.jsonArray, retryCnt = 3)
                }
                return
            }
        }

        if (autoReply) messageList.add(mapOf(
            "type" to "reply",
            "data" to mapOf(
                "id" to msgHash
            )
        ).json) // 添加回复
        if (MsgConstant.KCHATTYPEGROUP == record.chatType && atSender) {
            messageList.add(mapOf(
                "type" to "at",
                "data" to mapOf(
                    "qq" to record.senderUin
                )
            ).json) // 添加@发送者
        }
        messageList.addAll(message)
        MsgSvc.sendToAio(record.chatType, record.peerUin.toString(), JsonArray(messageList), retryCnt = 3)
    }
}
package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("set_group_kick", ["kick_group_member"])
internal object KickTroopMember: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getLong("group_id")
        val userId = session.getLong("user_id")
        val kickMsg = session.getStringOrNull("kick_msg") ?: ""
        val rejectAddRequest = session.getBooleanOrDefault("reject_add_request", false)

        return invoke(groupId, userId, rejectAddRequest, kickMsg, session.echo)
    }

    operator fun invoke(groupId: Long, userId: Long, rejectAddRequest: Boolean = false, kickMsg: String, echo: JsonElement = EmptyJsonString): String {
        GroupSvc.kickMember(groupId, rejectAddRequest, kickMsg, userId)
        return ok("成功", echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id", "user_id")
}
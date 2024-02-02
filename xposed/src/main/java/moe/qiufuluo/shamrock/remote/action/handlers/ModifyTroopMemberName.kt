package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("set_group_card")
internal object ModifyTroopMemberName: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        val userId = session.getString("user_id")
        val name = session.getStringOrNull("card") ?: ""
        return invoke(groupId, userId, name, session.echo)
    }

    operator fun invoke(groupId: String, userId: String, card: String, echo: JsonElement = EmptyJsonString): String {
        if (!GroupSvc.isAdmin(groupId)) {
            return logic("you are not admin", echo)
        }
        return if(GroupSvc.modifyGroupMemberCard(groupId.toLong(), userId.toLong(), card))
            ok("成功", echo)
        else error("check if member or group exist", echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id", "user_id")
}
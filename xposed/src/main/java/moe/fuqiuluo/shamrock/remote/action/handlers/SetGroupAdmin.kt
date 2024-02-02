package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("set_group_admin")
internal object SetGroupAdmin: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getLong("group_id")
        val userId = session.getLong("user_id")
        val enable = session.getBoolean("enable")
        return invoke(groupId, userId, enable, session.echo)
    }

    operator fun invoke(groupId: Long, userId: Long, enable: Boolean, echo: JsonElement = EmptyJsonString): String {
        if (!GroupSvc.isOwner(groupId.toString())) {
            return logic("you are not owner", echo)
        }
        GroupSvc.setGroupAdmin(groupId, userId, enable)
        return ok("成功", echo)
    }
}
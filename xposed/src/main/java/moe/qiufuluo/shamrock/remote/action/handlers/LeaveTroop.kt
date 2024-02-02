package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("leave_group", ["set_group_leave"])
internal object LeaveTroop: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        return invoke(groupId, session.echo)
    }

    operator fun invoke(groupId: String, echo: JsonElement = EmptyJsonString): String {
        if (GroupSvc.isOwner(groupId)) {
            return error("you are the owner of this group", echo)
        }
        GroupSvc.resignTroop(groupId.toLong())
        return ok("成功", echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id")
}
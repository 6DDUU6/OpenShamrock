package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("set_group_name")
internal object ModifyTroopName: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        val groupName = session.getString("group_name")

        return invoke(groupId, groupName, session.echo)
    }

    operator fun invoke(groupId: String, name: String, echo: JsonElement = EmptyJsonString): String {
        return if (GroupSvc.isAdmin(groupId)) {
            GroupSvc.modifyTroopName(groupId, name)
            ok("成功", echo)
        } else {
            logic("You are not the administrator of the group", echo)
        }
    }

    override val requiredParams: Array<String> = arrayOf("group_id", "group_name")
}
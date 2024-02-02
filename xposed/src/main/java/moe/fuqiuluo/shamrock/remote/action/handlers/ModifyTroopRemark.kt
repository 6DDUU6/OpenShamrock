package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("set_group_remark", ["modify_group_remark"])
internal object ModifyTroopRemark: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        val remark = session.getStringOrNull("remark") ?: ""
        return invoke(groupId, remark, session.echo)
    }

    operator fun invoke(groupId: String, remark: String, echo: JsonElement = EmptyJsonString): String {
        return if(GroupSvc.modifyGroupRemark(groupId.toLong(), remark))
            ok("成功", echo)
        else error("check if member or group exist", echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id")
}
package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("set_group_whole_ban")
internal object SetGroupWholeBan: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getLong("group_id")
        val enable = session.getBoolean("enable")
        return invoke(groupId, enable, session.echo)
    }

    operator fun invoke(groupId: Long, enable: Boolean, echo: JsonElement = EmptyJsonString): String {
        GroupSvc.setGroupWholeBan(groupId, enable)
        return ok("成功", echo)
    }

    override val requiredParams: Array<String> = arrayOf()
}
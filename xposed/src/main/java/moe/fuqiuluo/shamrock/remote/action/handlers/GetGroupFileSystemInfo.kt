package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.FileSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_group_file_system_info")
internal object GetGroupFileSystemInfo: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        return invoke(groupId, session.echo)
    }

    suspend operator fun invoke(groupId: String, echo: JsonElement = EmptyJsonString): String {
        return ok(data = FileSvc.getGroupFileSystemInfo(groupId.toLong()), echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id")
}
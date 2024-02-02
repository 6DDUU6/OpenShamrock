package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.FileSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_group_file_url")
internal object GetGroupFileUrl: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        val fileId = session.getString("file_id")
        val busid = session.getInt("busid")
        return invoke(groupId, fileId, busid, session.echo)
    }

    suspend operator fun invoke(groupId: String, fileId: String, busid: Int, echo: JsonElement = EmptyJsonString): String {
        return ok(data = FileSvc.getGroupFileInfo(groupId.toLong(), fileId, busid), echo = echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id", "file_id", "busid")
}
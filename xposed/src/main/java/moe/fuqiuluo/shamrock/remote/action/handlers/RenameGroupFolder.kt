package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.FileSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("rename_group_folder")
internal object RenameGroupFolder: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        val folderId = session.getString("folder_id")
        val name = session.getString("name")
        return invoke(groupId, folderId, name, session.echo)
    }

    suspend operator fun invoke(groupId: String, folderId: String, name: String, echo: JsonElement = EmptyJsonString): String {
        if (!FileSvc.renameFolder(groupId, folderId, name)) {
            return error("rename folder failed", echo = echo)
        }
        return ok("success", echo = echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id", "folder_id", "name")
}
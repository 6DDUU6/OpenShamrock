package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.FileSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("delete_group_file")
internal object DeleteGroupFile: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        val fileId = session.getString("file_id")
        val busid = session.getInt("busid")
        return invoke(groupId, fileId, busid, session.echo)
    }

    suspend operator fun invoke(groupId: String, fileId: String, bizId: Int, echo: JsonElement = EmptyJsonString): String {
        if(!FileSvc.deleteGroupFile(groupId, bizId, fileId)) {
            return error("删除失败", echo = echo)
        }
        return ok("成功", echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id", "file_id", "busid")
}
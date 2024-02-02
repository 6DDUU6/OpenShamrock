package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_prohibited_member_list")
internal object GetProhibitedMemberList: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupCode = session.getLong("group_id")
        return invoke(groupCode, session.echo)
    }

    suspend operator fun invoke(
        groupCode: Long,
        echo: JsonElement = EmptyJsonString
    ): String {
        val result = GroupSvc.getProhibitedMemberList(groupCode)
        if (result.isFailure) {
            return error(result.exceptionOrNull()?.message ?: "获取禁言列表失败", echo, arrayResult = true)
        }
        return ok(result.getOrThrow(), echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id")
}
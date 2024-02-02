package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("send_group_sign")
internal object SendGroupSign: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getLong("group_id")
        return invoke(groupId, session.echo)
    }

    suspend operator fun invoke(groupId: Long, echo: JsonElement = EmptyJsonString): String {
        val ret = GroupSvc.groupSign(groupId)
        return if (ret.isSuccess) {
            ok(ret.getOrNull() ?: "", echo)
        } else {
            logic(ret.exceptionOrNull()?.message ?: "", echo)
        }
    }

    override val requiredParams: Array<String> = arrayOf("group_id")
}
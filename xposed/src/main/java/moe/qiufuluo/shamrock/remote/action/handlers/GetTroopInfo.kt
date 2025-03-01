package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.GroupSvc
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.service.data.SimpleTroopInfo
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_group_info")
internal object GetTroopInfo: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        val refresh = session.getBooleanOrDefault("refresh", false)
        return invoke(groupId, refresh, session.echo)
    }

    suspend operator fun invoke(groupId: String, refresh: Boolean, echo: JsonElement = EmptyJsonString): String {
        val groupInfo = GroupSvc.getGroupInfo(groupId, refresh).getOrNull()
        return if ( groupInfo == null || groupInfo.troopuin.isNullOrBlank()) {
            logic("Unable to obtain group information", echo)
        } else {
            ok(SimpleTroopInfo(
                groupId = groupInfo.troopuin.toLong(),
                groupUin = groupInfo.troopcode.toLong(),
                groupName = groupInfo.troopname ?: groupInfo.newTroopName ?: groupInfo.oldTroopName,
                groupRemark = groupInfo.troopRemark,
                adminList = GroupSvc.getAdminList(groupId, true),
                classText = groupInfo.mGroupClassExtText,
                isFrozen = groupInfo.mIsFreezed != 0,
                maxMember = groupInfo.wMemberMax,
                memNum = groupInfo.wMemberNum,
                memCount = groupInfo.wMemberNum,
                maxNum = groupInfo.wMemberMax,
            ), echo)
        }
    }

    override val requiredParams: Array<String> = arrayOf("group_id")
}
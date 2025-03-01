package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.service.data.FriendEntry
import moe.qiufuluo.shamrock.remote.service.data.PlatformType
import moe.qiufuluo.qqinterface.servlet.FriendSvc
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_friend_list")
internal object GetFriendList: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val refresh = session.getBooleanOrDefault("refresh", false)
        return invoke(refresh, session.echo)
    }

    suspend operator fun invoke(refresh: Boolean, echo: JsonElement = EmptyJsonString): String {
        val friendList = FriendSvc.getFriendList(refresh).onFailure {
            return error(it.message ?: "unknown error", echo, arrayResult = true)
        }.getOrThrow()
        return ok(friendList.map { friend ->
            FriendEntry(
                id = friend.uin.toLong(),
                name = friend.name,
                displayName = friend.remark,
                remark = friend.remark,
                age = friend.age,
                gender = friend.gender,
                groupId = friend.groupid,
                platformType = PlatformType.valueOf(friend.iTermType),
                termType = friend.iTermType
            )
        }, echo)
    }
}
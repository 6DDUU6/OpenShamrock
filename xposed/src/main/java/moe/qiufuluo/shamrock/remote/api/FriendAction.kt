package moe.qiufuluo.shamrock.remote.api

import com.tencent.mobileqq.profilecard.api.IProfileCardBlacklistApi
import com.tencent.mobileqq.qroute.QRoute
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import moe.qiufuluo.shamrock.remote.action.handlers.GetFriendList
import moe.qiufuluo.shamrock.remote.action.handlers.GetFriendSystemMsg
import moe.qiufuluo.shamrock.remote.action.handlers.GetStrangerInfo
import moe.qiufuluo.shamrock.remote.action.handlers.IsBlackListUin
import moe.qiufuluo.shamrock.tools.fetchGetOrThrow
import moe.qiufuluo.shamrock.tools.fetchOrNull
import moe.qiufuluo.shamrock.tools.fetchOrThrow
import moe.qiufuluo.shamrock.tools.getOrPost

fun Routing.friendAction() {
    getOrPost("/get_stranger_info") {
        val uin = fetchOrThrow("user_id")
        call.respondText(GetStrangerInfo(uin), ContentType.Application.Json)
    }

    getOrPost("/get_friend_list") {
        val refresh = fetchOrNull("refresh")?.toBooleanStrictOrNull() ?: false
        call.respondText(GetFriendList(refresh), ContentType.Application.Json)
    }

    getOrPost("/is_blacklist_uin") {
        val uin = fetchOrThrow("user_id")
        call.respondText(IsBlackListUin(uin), ContentType.Application.Json)
    }

    getOrPost("/get_friend_system_msg") {
        call.respondText(GetFriendSystemMsg(), ContentType.Application.Json)
    }

}
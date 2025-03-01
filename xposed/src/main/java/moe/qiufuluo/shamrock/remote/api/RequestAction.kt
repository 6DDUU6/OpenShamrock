package moe.qiufuluo.shamrock.remote.api

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import moe.qiufuluo.shamrock.remote.action.handlers.SetFriendAddRequest
import moe.qiufuluo.shamrock.remote.action.handlers.SetGroupAddRequest
import moe.qiufuluo.shamrock.tools.fetchOrNull
import moe.qiufuluo.shamrock.tools.fetchOrThrow
import moe.qiufuluo.shamrock.tools.getOrPost

fun Routing.requestRouter() {
    getOrPost("/set_friend_add_request") {
        val flag = fetchOrThrow("flag")
        val approve = fetchOrNull("approve")?.toBooleanStrict() ?: true
        val remark = fetchOrNull("remark")
        val notSeen = fetchOrNull("not_seen")?.toBooleanStrict() ?: false

        call.respondText(
            SetFriendAddRequest(flag, approve, remark, notSeen),
            ContentType.Application.Json
        )
    }

    getOrPost("/set_group_add_request") {
        val flag = fetchOrThrow("flag")
        val approve = fetchOrNull("approve")?.toBooleanStrict() ?: true
        val remark = fetchOrNull("reason")
        val subType = fetchOrThrow("sub_type")
        val notSeen = fetchOrNull("not_seen")?.toBooleanStrict() ?: false

        call.respondText(
            SetGroupAddRequest(flag, approve, subType, remark, notSeen),
            ContentType.Application.Json
        )
    }

}
package moe.qiufuluo.shamrock.remote.api

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import moe.qiufuluo.shamrock.tools.fetchOrNull
import moe.qiufuluo.shamrock.tools.getOrPost
import moe.qiufuluo.shamrock.helper.LogCenter

fun Routing.showLog() {
    getOrPost("/log") {
        val start = fetchOrNull("start")?.toIntOrNull() ?: 0
        val recent =fetchOrNull("recent")?.toBooleanStrictOrNull() ?: false
        val log = LogCenter.getLogLines(start, recent)
        call.respondText(log.joinToString("\n"))
    }
}
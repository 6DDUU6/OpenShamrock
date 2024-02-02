package moe.qiufuluo.shamrock.remote.api

import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.coroutines.delay
import moe.qiufuluo.shamrock.remote.structures.Status
import moe.qiufuluo.shamrock.tools.getOrPost
import moe.qiufuluo.shamrock.tools.respond
import moe.qiufuluo.shamrock.helper.LogCenter
import kotlin.system.exitProcess

fun Routing.obtainFrameworkInfo() {
    getOrPost("/get_start_time") {
        respond(
            isOk = true,
            code = Status.Ok,
            moe.qiufuluo.shamrock.remote.HTTPServer.startTime
        )
    }

    get("/shut") {
        moe.qiufuluo.shamrock.remote.HTTPServer.stop()
        LogCenter.log("正在关闭Shamrock。", toast = true)
        delay(3000)
        exitProcess(0)
    }
}

package moe.qiufuluo.shamrock.remote.config

import moe.qiufuluo.shamrock.helper.ErrorTokenException
import moe.qiufuluo.shamrock.helper.LogicException
import moe.qiufuluo.shamrock.helper.ParamsException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.helper.Level
import moe.qiufuluo.shamrock.helper.LogCenter
import moe.qiufuluo.shamrock.remote.structures.CommonResult
import moe.qiufuluo.shamrock.remote.structures.ErrorCatch
import moe.qiufuluo.shamrock.remote.structures.Status

val ECHO_KEY = AttributeKey<JsonElement>("echo")

fun Application.statusPages() {
    install(StatusPages) {
        exception<ParamsException> { call, cause ->
            val echo = if (call.attributes.contains(ECHO_KEY)) {
                call.attributes[ECHO_KEY]
            } else null
            call.respond(
                CommonResult(
                status = "failed",
                retcode = Status.BadParam.code,
                data = ErrorCatch(call.request.uri, cause.message ?: ""),
                echo = echo
            )
            )
        }
        exception<LogicException> { call, cause ->
            val echo = if (call.attributes.contains(ECHO_KEY)) {
                call.attributes[ECHO_KEY]
            } else null
            call.respond(
                CommonResult(
                status = "failed",
                retcode = Status.LogicError.code,
                data = ErrorCatch(call.request.uri, cause.message ?: ""),
                echo = echo
            )
            )
        }
        exception<ErrorTokenException> { call, cause ->
            val echo = if (call.attributes.contains(ECHO_KEY)) {
                call.attributes[ECHO_KEY]
            } else null
            call.respond(
                CommonResult(
                status = "failed",
                retcode = Status.ErrorToken.code,
                data = ErrorCatch(call.request.uri, cause.message ?: ""),
                echo = echo
            )
            )
        }
        exception<Throwable> { call, cause ->
            val echo = if (call.attributes.contains(ECHO_KEY)) {
                call.attributes[ECHO_KEY]
            } else null
            LogCenter.log(cause.stackTraceToString(), Level.ERROR)
            call.respond(
                CommonResult(
                status = "failed",
                retcode = Status.InternalHandlerError.code,
                data = ErrorCatch(call.request.uri, cause.message ?: ""),
                echo = echo
            )
            )
        }
    }
}
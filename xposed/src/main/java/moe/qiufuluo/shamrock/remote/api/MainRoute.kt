package moe.qiufuluo.shamrock.remote.api

import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.httpVersion
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import moe.qiufuluo.shamrock.remote.HTTPServer
import moe.qiufuluo.shamrock.remote.action.ActionManager
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.config.ECHO_KEY
import moe.qiufuluo.shamrock.remote.structures.EmptyObject
import moe.qiufuluo.shamrock.remote.structures.IndexData
import moe.qiufuluo.shamrock.remote.structures.Status
import moe.qiufuluo.shamrock.tools.EmptyJsonObject
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.shamrock.tools.asJsonObjectOrNull
import moe.qiufuluo.shamrock.tools.asString
import moe.qiufuluo.shamrock.tools.fetchOrNull
import moe.qiufuluo.shamrock.tools.fetchOrThrow
import moe.qiufuluo.shamrock.tools.fetchPostJsonElement
import moe.qiufuluo.shamrock.tools.fetchPostJsonElementOrNull
import moe.qiufuluo.shamrock.tools.fetchPostJsonObjectOrNull
import moe.qiufuluo.shamrock.tools.isJsonArray
import moe.qiufuluo.shamrock.tools.isJsonObject
import moe.qiufuluo.shamrock.tools.json
import moe.qiufuluo.shamrock.tools.respond
import moe.qiufuluo.shamrock.utils.PlatformUtils
import mqq.app.MobileQQ

@Serializable
data class OldApiResult<T>(
    val code: Int,
    val msg: String = "",
    @Contextual
    val data: T? = null
)

suspend fun PipelineContext<Unit, ApplicationCall>.handleAsJsonObject(data: JsonObject) {
    val action = data["action"].asString
    val echo = data["echo"] ?: EmptyJsonString
    call.attributes.put(ECHO_KEY, echo)

    val params = data["params"].asJsonObjectOrNull ?: EmptyJsonObject

    val handler = ActionManager[action]
    if (handler == null) {
        respond(false, Status.UnsupportedAction, EmptyObject, "不支持的Action", echo = echo)
    } else {
        call.respondText(handler.handle(ActionSession(params, echo)), ContentType.Application.Json)
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.handleAsJsonArray(data: JsonArray) {
    data.forEach {
        when (it) {
            is JsonArray -> handleAsJsonArray(it)
            is JsonObject -> handleAsJsonObject(it)
            else -> handleAsJsonObject(it.jsonObject)
        }
    }
}

fun Routing.echoVersion() {
    route("/") {
        get {
            respond(
                isOk = true,
                code = Status.Ok,
                data = IndexData(PlatformUtils.getClientVersion(MobileQQ.getContext()), HTTPServer.startTime, call.request.httpVersion)
            )
        }
        post {
            fetchPostJsonElementOrNull()?.let {
                if (it is JsonArray) {
                    handleAsJsonArray(it)
                    return@post
                } else if (it is JsonObject) {
                    handleAsJsonObject(it)
                    return@post
                }
            }
            val action = fetchOrThrow("action")
            val echo = if (isJsonObject("echo") || isJsonArray("echo")) {
                fetchPostJsonElement("echo")
            } else {
                (fetchOrNull("echo") ?: "").json
            }
            call.attributes.put(ECHO_KEY, echo)

            val params = fetchPostJsonObjectOrNull("params") ?: EmptyJsonObject

            val handler = ActionManager[action]
            if (handler == null) {
                respond(false, Status.UnsupportedAction, EmptyObject, "不支持的Action", echo = echo)
            } else {
                call.respondText(handler.handle(ActionSession(params, echo)), ContentType.Application.Json)
            }
        }
    }
}
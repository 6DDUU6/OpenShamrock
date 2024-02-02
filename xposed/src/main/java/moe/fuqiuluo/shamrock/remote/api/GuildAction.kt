package moe.qiufuluo.shamrock.remote.api

import com.tencent.mobileqq.qqguildsdk.api.IGPSService
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import moe.qiufuluo.shamrock.remote.structures.EmptyObject
import moe.qiufuluo.shamrock.tools.getOrPost
import moe.qiufuluo.shamrock.tools.respond
import moe.qiufuluo.shamrock.xposed.helper.AppRuntimeFetcher
import moe.qiufuluo.shamrock.xposed.helper.NTServiceFetcher

fun Routing.guildAction() {
    getOrPost("/get_guild_service_profile") {
        val service = AppRuntimeFetcher.appRuntime
            .getRuntimeService(IGPSService::class.java, "all")
        val tinyId = service.selfTinyId

    }

    getOrPost("/refresh_guild_list") {
        val kernelService = NTServiceFetcher.kernelService
        val sessionService = kernelService.wrapperSession
        val guildService = sessionService.guildService
        guildService.refreshGuildList(true)
        respond(false, -100, msg = "测试接口", data = EmptyObject)
    }

    getOrPost("/get_guild_list") {

        call.respondText("ok")
    }
}
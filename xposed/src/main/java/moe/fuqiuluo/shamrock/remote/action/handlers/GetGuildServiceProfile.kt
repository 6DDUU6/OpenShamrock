package moe.qiufuluo.shamrock.remote.action.handlers

import com.tencent.mobileqq.qqguildsdk.api.IGPSService
import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonObject
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.shamrock.xposed.helper.AppRuntimeFetcher
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_guild_service_profile")
internal object GetGuildServiceProfile : IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(echo = session.echo)
    }

    operator fun invoke(echo: JsonElement = EmptyJsonString): String {
        // TODO: get_guild_service_profile
        return ok(EmptyJsonObject, echo, "此功能尚未实现")

        val service = AppRuntimeFetcher.appRuntime
            .getRuntimeService(IGPSService::class.java, "all")
        if (!service.isGProSDKInitCompleted) {
            return error("GPro服务没有初始化", echo = echo)
        }

        val tinyId = service.selfTinyId

        return ok(echo = echo)
    }
}
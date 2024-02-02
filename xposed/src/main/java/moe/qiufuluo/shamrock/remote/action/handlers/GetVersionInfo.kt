package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.service.data.VersionInfo
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.shamrock.tools.ShamrockVersion
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_version_info", ["get_version"])
internal object GetVersionInfo : IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(session.echo)
    }

    operator fun invoke(echo: JsonElement = EmptyJsonString): String {
        return ok(
            VersionInfo(
                appFullName = "Shamrock v$ShamrockVersion",
                appName = "Shamrock",
                appVersion = ShamrockVersion,
                impl = "shamrock",
                version = ShamrockVersion,
                onebotVersion = "11",
            ),
            echo = echo
        )
    }
}
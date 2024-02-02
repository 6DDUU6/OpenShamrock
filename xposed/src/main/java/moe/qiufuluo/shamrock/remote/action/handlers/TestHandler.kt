package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.Serializable
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.action.ActionSession
import de.robv.android.xposed.XposedBridge.log
import moe.qiufuluo.shamrock.remote.structures.Status
import moe.qiufuluo.shamrock.remote.structures.resultToString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("test")
internal object TestHandler: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        kotlin.runCatching {
            val msg = StringBuffer()
            return resultToString(
                isOk = true,
                code = Status.Ok,
                data = Test(System.currentTimeMillis()),
                msg = msg.toString(),
                echo = session.echo
            )
        }.onFailure {
            log(it)
        }
        return "error"
    }

    @Serializable
    data class Test(val time: Long)
}
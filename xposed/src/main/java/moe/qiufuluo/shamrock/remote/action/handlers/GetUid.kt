package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.coroutines.suspendCancellableCoroutine
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.structures.Status
import moe.qiufuluo.shamrock.remote.structures.resultToString
import moe.qiufuluo.shamrock.tools.asString
import moe.qiufuluo.shamrock.xposed.helper.NTServiceFetcher
import moe.qiufuluo.symbols.OneBotHandler
import kotlin.coroutines.resume

@OneBotHandler("get_uid")
internal object GetUid: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val kernelService = NTServiceFetcher.kernelService
        val sessionService = kernelService.wrapperSession
        val uinList = session.getArray("uin_list").map {
            it.asString.toLong()
        }

        val uidMap = suspendCancellableCoroutine { continuation ->
            sessionService.uixConvertService.getUid(uinList.toHashSet()) {
                continuation.resume(it)
            }
        }
        return resultToString(true, Status.Ok, uidMap, echo = session.echo)
    }
}
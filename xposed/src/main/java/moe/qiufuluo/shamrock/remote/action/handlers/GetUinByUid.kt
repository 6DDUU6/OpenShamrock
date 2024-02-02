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

@OneBotHandler("get_uin_by_uid")
internal object GetUinByUid: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val kernelService = NTServiceFetcher.kernelService
        val sessionService = kernelService.wrapperSession
        val uidList = session.getArray("uid_list").map {
            it.asString
        }
        val uinMap = suspendCancellableCoroutine { continuation ->
            sessionService.uixConvertService.getUin(uidList.toHashSet()) {
                continuation.resume(it)
            }
        }
        return resultToString(true, Status.Ok, uinMap, echo = session.echo)
    }
}
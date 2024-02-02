@file:OptIn(DelicateCoroutinesApi::class)

package moe.qiufuluo.shamrock.xposed.hooks

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.DelicateCoroutinesApi
import moe.qiufuluo.shamrock.utils.PlatformUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.qiufuluo.shamrock.tools.broadcast
import moe.qiufuluo.shamrock.helper.Level
import moe.qiufuluo.shamrock.helper.LogCenter
import moe.qiufuluo.shamrock.xposed.helper.internal.*
import moe.qiufuluo.shamrock.xposed.ipc.ShamrockIpc
import moe.qiufuluo.symbols.Process
import moe.qiufuluo.symbols.XposedHook

@XposedHook(Process.MSF, priority = 0)
internal class IpcService: IAction {
    override fun invoke(ctx: Context) {
        if (!PlatformUtils.isMsfProcess()) return
        initIPCFetcher(ctx)
    }

    private fun initIPCFetcher(ctx: Context) {
        LogCenter.log("IPC服务已启动：$ctx", Level.INFO)
        DynamicReceiver.register("fetch_ipc", IPCRequest {
            val name = it.getStringExtra("ipc_name")
            LogCenter.log("IPC FETCH => $name，请注意是否泄露了API？")
            GlobalScope.launch {
                ShamrockIpc.get(name)?.also { binder ->
                    ctx.broadcast("xqbot") {
                        putExtra("__cmd", "ipc_callback")
                        putExtra("ipc", Bundle().also {
                            it.putString("name", name)
                            it.putBinder("binder", binder)
                        })
                    }
                } ?: LogCenter.log("无法获取IPC: $name", Level.WARN)
            }
        })
    }
}

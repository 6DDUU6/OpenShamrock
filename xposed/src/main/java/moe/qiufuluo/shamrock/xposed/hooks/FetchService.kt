@file:OptIn(DelicateCoroutinesApi::class)
package moe.qiufuluo.shamrock.xposed.hooks

import android.content.Context
import com.tencent.qqnt.kernel.api.IKernelService
import com.tencent.qqnt.kernel.api.impl.KernelServiceImpl
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.qiufuluo.shamrock.tools.hookMethod
import moe.qiufuluo.shamrock.utils.PlatformUtils
import moe.qiufuluo.shamrock.helper.Level
import moe.qiufuluo.shamrock.helper.LogCenter
import moe.qiufuluo.shamrock.xposed.helper.NTServiceFetcher
import moe.qiufuluo.shamrock.xposed.loader.NativeLoader
import moe.qiufuluo.symbols.XposedHook

@XposedHook(priority = 2)
internal class FetchService: IAction {
    override fun invoke(ctx: Context) {
        NativeLoader.load("shamrock")

        if (PlatformUtils.isMqq()) {
            KernelServiceImpl::class.java.hookMethod("initService").after {
                val service = it.thisObject as IKernelService
                LogCenter.log("NTKernel try to init service: $service", Level.DEBUG)
                GlobalScope.launch {
                    NTServiceFetcher.onFetch(service)
                }
            }
        } else if (PlatformUtils.isTim()) {
            // TIM 尚未进入 NTKernel
            LogCenter.log("NTKernel init failed: tim not support NT", Level.ERROR)
        }

    }
}
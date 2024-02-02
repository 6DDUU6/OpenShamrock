@file:Suppress("UNUSED_VARIABLE", "LocalVariableName")
package moe.qiufuluo.shamrock.xposed.hooks

import android.content.Context
import com.tencent.common.config.pad.DeviceType
import com.tencent.qqnt.kernel.nativeinterface.InitSessionConfig
import de.robv.android.xposed.XposedBridge
import moe.qiufuluo.shamrock.remote.service.config.ShamrockConfig
import moe.qiufuluo.shamrock.tools.FuzzySearchClass
import moe.qiufuluo.shamrock.utils.PlatformUtils
import moe.qiufuluo.shamrock.helper.LogCenter
import moe.qiufuluo.shamrock.tools.afterHook
import moe.qiufuluo.shamrock.tools.hookMethod
import moe.qiufuluo.shamrock.xposed.loader.LuoClassloader
import moe.qiufuluo.symbols.XposedHook

@XposedHook(priority = 0)
internal class ForceTablet: IAction {
    override fun invoke(ctx: Context) {
        //if (!PlatformUtils.isMqqPackage()) return
        if (ShamrockConfig.forceTablet()) {
            if (PlatformUtils.isMainProcess()) {
                LogCenter.log("强制协议类型 (PAD)", toast = true)
            }

            val returnTablet = afterHook {
                it.result = DeviceType.TABLET
            }

            FuzzySearchClass.findAllClassByMethod(
                LuoClassloader.hostClassLoader, "com.tencent.common.config.pad"
            ) { _, method ->
                method.returnType == DeviceType::class.java
            }.forEach { clazz ->
                //log("Inject to tablet mode in ${clazz.name}")
                val method = clazz.declaredMethods.first { it.returnType == DeviceType::class.java }
                XposedBridge.hookMethod(method, returnTablet)
            }

            val PadUtil = LuoClassloader.load("com.tencent.common.config.pad.PadUtil")
            PadUtil?.declaredMethods?.filter {
                it.returnType == DeviceType::class.java
            }?.forEach {
                XposedBridge.hookMethod(it, returnTablet)
            }

            val deviceTypeField = InitSessionConfig::class.java.declaredFields.firstOrNull {
                it.type == com.tencent.qqnt.kernel.nativeinterface.DeviceType::class.java
            }
            if (deviceTypeField != null) {
                XposedBridge.hookAllConstructors(InitSessionConfig::class.java, afterHook {
                    if (!deviceTypeField.isAccessible) deviceTypeField.isAccessible = true
                    deviceTypeField.set(it.thisObject, com.tencent.qqnt.kernel.nativeinterface.DeviceType.KPAD)
                })
            }
            InitSessionConfig::class.java.hookMethod("getDeviceType").after {
                it.result = com.tencent.qqnt.kernel.nativeinterface.DeviceType.KPAD
            }

            //InitSessionConfig::class.java.hookMethod("getPlatform").after {
            //    it.result = PlatformType.KMAC
            //}
        }
    }
}
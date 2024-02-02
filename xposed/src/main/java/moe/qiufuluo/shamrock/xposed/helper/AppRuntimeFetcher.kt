package moe.qiufuluo.shamrock.xposed.helper

import moe.qiufuluo.shamrock.utils.PlatformUtils
import mqq.app.AppRuntime
import mqq.app.MobileQQ

internal object AppRuntimeFetcher {
    val appRuntime: AppRuntime
        get() = if (PlatformUtils.isMqqPackage())
            MobileQQ.getMobileQQ().waitAppRuntime()
        else
            MobileQQ.getMobileQQ().waitAppRuntime(null)
}
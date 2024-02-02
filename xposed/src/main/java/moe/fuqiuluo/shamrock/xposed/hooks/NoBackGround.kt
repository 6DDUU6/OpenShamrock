package moe.qiufuluo.shamrock.xposed.hooks

import android.content.Context
import de.robv.android.xposed.XposedHelpers
import moe.qiufuluo.shamrock.tools.hookMethod
import moe.qiufuluo.shamrock.helper.Level
import moe.qiufuluo.shamrock.helper.LogCenter
import moe.qiufuluo.shamrock.xposed.loader.LuoClassloader
import moe.qiufuluo.symbols.XposedHook
import mqq.app.MobileQQ

@XposedHook(priority = 10)
internal class NoBackGround: IAction {
    override fun invoke(ctx: Context) {
        kotlin.runCatching {
            XposedHelpers.findClass("com.tencent.mobileqq.activity.miniaio.MiniMsgUser", LuoClassloader)
        }.onSuccess {
            it.hookMethod("onBackground").before {
                it.result = null
            }
        }.onFailure {
            LogCenter.log("Keeping MiniMsgUser alive failed: ${it.message}", Level.WARN)
        }

        try {
            val application = MobileQQ.getMobileQQ()
            application.javaClass.hookMethod("onActivityFocusChanged").before {
                it.args[1] = true
            }
        } catch (e: Throwable) {
            LogCenter.log("Keeping MSF alive failed: ${e.message}", Level.WARN)
        }
    }
}
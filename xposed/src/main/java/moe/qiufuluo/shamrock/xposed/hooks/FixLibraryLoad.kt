package moe.qiufuluo.shamrock.xposed.hooks

import android.content.Context
import moe.qiufuluo.shamrock.tools.hookMethod
import moe.qiufuluo.shamrock.xposed.loader.NativeLoader
import moe.qiufuluo.symbols.XposedHook

@XposedHook(priority = 0)
internal class FixLibraryLoad: IAction {
    val redirectedLibrary =arrayOf(
        "ffmpegkit_abidetect",
        "avutil",
        "swscale",
        "swresample",
        "avcodec",
        "avformat",
        "avfilter",
        "avdevice",
        "ffmpegkit"
    )

    override fun invoke(ctx: Context) {
        com.arthenica.ffmpegkit.NativeLoader::class.java.hookMethod("loadLibrary").before {
            val name: String = it.args[0] as String
            if (name in redirectedLibrary) {
                redirectedLibrary.forEach {
                    NativeLoader.load(it)
                }
                NativeLoader.load(name)
            }
            it.result = null
        }
    }
}
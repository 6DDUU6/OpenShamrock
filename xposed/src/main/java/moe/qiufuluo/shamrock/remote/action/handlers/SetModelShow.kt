package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.qqinterface.servlet.CardSvc
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.shamrock.utils.PlatformUtils
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("_set_model_show")
internal object SetModelShow : IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val model = session.getString("model")
        val manu = session.getStringOrNull("manu") ?: session.getString("model_show")
        val modelShow = session.getStringOrNull("modelshow") ?: "Android"
        val imei = session.getStringOrNull("imei") ?: PlatformUtils.getAndroidID()
        val show = session.getBooleanOrDefault("show", true)
        return invoke(model, manu, modelShow, imei, show, session.echo)
    }

    suspend operator fun invoke(
        model: String,
        manu: String,
        modelShow: String,
        imei: String,
        show: Boolean,
        echo: JsonElement = EmptyJsonString
    ): String {
        CardSvc.setModelShow(model, manu, modelShow, imei, show)
        return ok("成功", echo = echo)
    }

    override val requiredParams: Array<String> = arrayOf("model")
}
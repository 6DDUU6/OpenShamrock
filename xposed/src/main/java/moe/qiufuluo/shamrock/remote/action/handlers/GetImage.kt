package moe.qiufuluo.shamrock.remote.action.handlers

import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.qqinterface.servlet.transfile.RichProtoSvc
import moe.qiufuluo.shamrock.helper.db.ImageDB
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("get_image")
internal object GetImage: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val echo = session.echo
        val file = session.getString("file")
        return invoke(file, echo)
    }

    operator fun invoke(file: String, echo: JsonElement = EmptyJsonString): String {
        val fileMd5 = file
            .replace("{", "")
            .replace("}", "")
            .replace("-", "")
            .split(".")[0].uppercase().trim()
        if (fileMd5.length != 32) {
            return badParam("图片缓存文件名不合法", echo = echo)
        }

        val image = ImageDB.getInstance().imageMappingDao().queryByFileName(fileMd5)
            ?: return logic("只能查询已缓存的图片", echo = echo)

        return ok(GetImageResult(
            image.size,
            image.fileName,
            when(image.chatType) {
                MsgConstant.KCHATTYPEGROUP -> RichProtoSvc.getGroupPicDownUrl(fileMd5)
                MsgConstant.KCHATTYPEC2C -> RichProtoSvc.getC2CPicDownUrl(fileMd5)
                else -> error("Not supported chat type: ${image.chatType}, convertMsgElementsToMsgSegment::Pic")
            }
        ), echo = echo)
    }

    override val requiredParams: Array<String> = arrayOf("file")

    @Serializable
    data class GetImageResult(
        val size: Long,
        val filename: String,
        val url: String
    )
}
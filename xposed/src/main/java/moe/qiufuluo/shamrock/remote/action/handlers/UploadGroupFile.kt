package moe.qiufuluo.shamrock.remote.action.handlers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import com.tencent.mobileqq.qroute.QRoute
import com.tencent.qqnt.kernel.nativeinterface.FileElement
import com.tencent.qqnt.kernel.nativeinterface.FileTransNotifyInfo
import com.tencent.qqnt.kernel.nativeinterface.MsgConstant
import com.tencent.qqnt.kernel.nativeinterface.MsgElement
import com.tencent.qqnt.msg.api.IMsgService
import com.tencent.qqnt.msg.api.IMsgUtilApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.helper.LogCenter
import moe.qiufuluo.shamrock.helper.MessageHelper
import moe.qiufuluo.shamrock.helper.TransfileHelper
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.remote.service.api.RichMediaUploadHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.shamrock.utils.FileUtils
import moe.qiufuluo.shamrock.utils.MD5
import moe.qiufuluo.symbols.OneBotHandler
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

@OneBotHandler("upload_group_file")
internal object UploadGroupFile : IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getString("group_id")
        val file = session.getString("file")
        val name = session.getString("name")
            .replace("/", "_")
            .replace("\\", "_")
            .replace("\n", "_")
            .replace("\t", "_")
            .replace("\r", "_")
        return invoke(groupId, file, name, session.echo)
    }

    suspend operator fun invoke(
        groupId: String,
        file: String,
        name: String,
        echo: JsonElement = EmptyJsonString
    ): String {
        var srcFile = File(file)
        if (!srcFile.exists()) {
            srcFile = FileUtils.getFile(file)
        }
        if (!srcFile.exists()) {
            return badParam("文件不存在", echo)
        }

        val fileElement = FileElement()
        fileElement.fileMd5 = ""
        fileElement.fileName = name
        fileElement.filePath = srcFile.absolutePath
        fileElement.fileSize = srcFile.length()
        fileElement.picWidth = 0
        fileElement.picHeight = 0
        fileElement.videoDuration = 0
        fileElement.picThumbPath = HashMap()
        fileElement.expireTime = 0L
        fileElement.fileSha = ""
        fileElement.fileSha3 = ""
        fileElement.file10MMd5 = ""
        when (TransfileHelper.getExtensionId(name)) {
            0 -> {
                val wh = QRoute.api(IMsgUtilApi::class.java)
                    .getPicSizeByPath(srcFile.absolutePath)
                fileElement.picWidth = wh.first
                fileElement.picHeight = wh.second
                fileElement.picThumbPath[750] = srcFile.absolutePath
            }
            2 -> {
                val thumbPic = FileUtils.getFile(MD5.genFileMd5Hex(srcFile.absolutePath))
                withContext(Dispatchers.IO) {
                    val fileOutputStream = FileOutputStream(thumbPic)
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(fileElement.filePath)
                    retriever.frameAtTime?.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)
                    fileOutputStream.flush()
                    fileOutputStream.close()
                }
                val options = BitmapFactory.Options()
                BitmapFactory.decodeFile(thumbPic.absolutePath, options)
                fileElement.picHeight = options.outHeight
                fileElement.picWidth = options.outWidth
                fileElement.picThumbPath = hashMapOf(750 to thumbPic.absolutePath)
            }
        }
        val msgElement = MsgElement()
        msgElement.elementType = MsgConstant.KELEMTYPEFILE
        msgElement.fileElement = fileElement

        // 根据文件大小调整超时时间
        val msgIdPair = MessageHelper.generateMsgId(MsgConstant.KCHATTYPEGROUP)
        val info = (withTimeoutOrNull((srcFile.length() / (300 * 1024)) * 1000 + 5000) {
            val msgService = QRoute.api(IMsgService::class.java)
            val contact = MessageHelper.generateContact(MsgConstant.KCHATTYPEGROUP, groupId)
            suspendCancellableCoroutine<FileTransNotifyInfo?> {
                msgService.sendMsgWithMsgId(
                    contact, msgIdPair.qqMsgId, arrayListOf(msgElement)
                ) { code, reason ->
                    LogCenter.log("群文件消息发送异常(code = $code, reason = $reason)")
                    it.resume(null)
                }
                RichMediaUploadHandler.registerListener(msgIdPair.qqMsgId) {
                    it.resume(this)
                    return@registerListener true
                }
            }
        } ?: return error("上传文件失败", echo)).also {
            if (it.commonFileInfo == null) {
                return error(it.fileErrMsg ?: "上传文件失败", echo)
            }
        }.commonFileInfo

        return ok(data = FileUploadResult(
            msgHash = msgIdPair.msgHashId,
            bizid = info.bizType ?: 0,
            md5 = info.md5,
            sha = info.sha,
            sha3 = info.sha3,
            fileId = info.uuid
        ), echo = echo)
    }

    override val requiredParams: Array<String> = arrayOf("group_id", "file", "name")

    @Serializable
    data class FileUploadResult(
        @SerialName("msg_id") val msgHash: Int,
        @SerialName("bizid") val bizid: Int,
        @SerialName("md5") val md5: String,
        @SerialName("sha") val sha: String,
        @SerialName("sha3") val sha3: String,
        @SerialName("file_id") val fileId: String
    )
}
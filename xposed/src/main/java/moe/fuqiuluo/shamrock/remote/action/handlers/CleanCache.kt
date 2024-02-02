package moe.qiufuluo.shamrock.remote.action.handlers

import kotlinx.serialization.json.JsonElement
import moe.qiufuluo.shamrock.utils.FileUtils
import moe.qiufuluo.shamrock.remote.action.ActionSession
import moe.qiufuluo.shamrock.remote.action.IActionHandler
import moe.qiufuluo.shamrock.tools.EmptyJsonString
import moe.qiufuluo.shamrock.utils.MMKVFetcher
import moe.qiufuluo.symbols.OneBotHandler

@OneBotHandler("clean_cache")
internal object CleanCache: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        return invoke(session.echo)
    }

    operator fun invoke(echo: JsonElement = EmptyJsonString): String {
        FileUtils.clearCache()
        MMKVFetcher.mmkvWithId("hash2id")
            .clear()
        MMKVFetcher.mmkvWithId("id2id")
            .clear()
        MMKVFetcher.mmkvWithId("seq2id")
            .clear()
        MMKVFetcher.mmkvWithId("audio2silk")
            .clear()
        return ok("成功", echo)
    }
}
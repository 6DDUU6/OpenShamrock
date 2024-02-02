package moe.qiufuluo.shamrock.remote.service.data

import kotlinx.serialization.Serializable

@Serializable
internal data class OutResource(
    val file: String,
    val url: String
)
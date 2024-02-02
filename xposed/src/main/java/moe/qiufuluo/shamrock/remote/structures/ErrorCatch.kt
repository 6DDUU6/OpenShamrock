package moe.qiufuluo.shamrock.remote.structures

import kotlinx.serialization.Serializable

@Serializable
data class ErrorCatch(
    var url: String,
    var error: String
)

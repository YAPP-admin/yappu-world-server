package co.yappuworld.global.dto

data class GlobalResponse(
    val isSuccess: Boolean,
    val message: String,
    val errorCode: String,
    val body: Any?
)

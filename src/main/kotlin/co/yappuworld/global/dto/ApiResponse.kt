package co.yappuworld.global.dto

data class ApiResponse<T : Any>(
    val isSuccess: Boolean,
    val message: String,
    val errorCode: String,
    val body: T?
)

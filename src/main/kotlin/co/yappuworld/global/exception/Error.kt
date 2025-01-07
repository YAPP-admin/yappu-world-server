package co.yappuworld.global.exception

interface Error {
    val message: String
    val code: String
    val type: ErrorType
}

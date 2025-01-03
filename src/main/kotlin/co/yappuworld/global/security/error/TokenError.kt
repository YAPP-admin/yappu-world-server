package co.yappuworld.global.security.error

enum class TokenError(
    val message: String,
    val code: String
) {
    EXPIRED_TOKEN(
        "만료된 토큰입니다.",
        "TKN-0001"
    ),
    INVALID_TOKEN(
        "비정상 토큰입니다.",
        "TKN-0002"
    )
}

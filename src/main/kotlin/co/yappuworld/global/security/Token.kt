package co.yappuworld.global.security

data class Token(
    val accessToken: String,
    val refreshToken: String?
)

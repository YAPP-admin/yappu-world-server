package co.yappuworld.global.security

data class Token(
    val accessToken: String,
    val refreshToken: String?
) {

    var isAccessTokenExpired: Boolean = false
        private set

    var isRefreshTokenExpired: Boolean = false
        private set

    fun expireAccessToken() {
        isAccessTokenExpired = true
    }

    fun expireRefreshToken() {
        isRefreshTokenExpired = true
    }
}

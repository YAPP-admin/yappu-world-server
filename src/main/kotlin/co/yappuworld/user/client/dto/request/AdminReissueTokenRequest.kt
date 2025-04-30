package co.yappuworld.user.client.dto.request

data class AdminReissueTokenRequest(
    val accessToken: String,
    val refreshToken: String
)

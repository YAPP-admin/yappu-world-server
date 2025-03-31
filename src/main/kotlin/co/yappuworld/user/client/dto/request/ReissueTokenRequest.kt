package co.yappuworld.user.client.dto.request

data class ReissueTokenRequest(
    val accessToken: String,
    val refreshToken: String
)

package co.yappuworld.global.security

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secretKey: String,
    val accessTokenExpirationTimes: Int,
    val refreshTokenExpirationTimes: Int
) {

    val base64UrlSecretKey = requireNotNull(
        Keys.hmacShaKeyFor(
            Decoders.BASE64URL.decode(secretKey)
        )
    )
}

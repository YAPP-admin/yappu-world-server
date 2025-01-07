package co.yappuworld.global.property

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperty(
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

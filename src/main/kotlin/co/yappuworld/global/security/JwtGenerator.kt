package co.yappuworld.global.security

import co.yappuworld.global.property.JwtProperty
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Component
class JwtGenerator(
    private val jwtProperty: JwtProperty
) {

    fun generateToken(
        securityUser: SecurityUser,
        now: LocalDateTime
    ): Token {
        val date = Date.from(now.atZone(ZoneId.of("Asia/Seoul")).toInstant())
        return Token(generateAccessToken(date), generateRefreshToken(date))
    }

    private fun generateAccessToken(now: Date): String {
        return Jwts.builder()
            .subject("AccessToken")
            .expiration(Date(now.time + jwtProperty.accessTokenExpirationTimes))
            .signWith(jwtProperty.base64UrlSecretKey)
            .compact()
    }

    private fun generateRefreshToken(now: Date): String {
        return Jwts.builder()
            .subject("RefreshToken")
            .expiration(Date(now.time + jwtProperty.refreshTokenExpirationTimes))
            .signWith(jwtProperty.base64UrlSecretKey)
            .compact()
    }
}

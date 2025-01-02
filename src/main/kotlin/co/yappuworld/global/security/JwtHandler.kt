package co.yappuworld.global.security

import co.yappuworld.global.property.JwtProperty
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger { }

@Component
class JwtHandler(
    private val jwtProperty: JwtProperty
) {

    fun extractSecurityUserOrNull(request: HttpServletRequest): SecurityUser? {
        val token = extractToken(request) ?: return null

        return try {
            SecurityUser.fromValidToken(token, parseToken(token.accessToken) as Map<String, String>)
        } catch (e: ExpiredJwtException) {
            token.expireAccessToken()
            SecurityUser.fromExpiredToken(token, e.claims as Map<String, String>)
        } catch (e: Exception) {
            null
        }
    }

    private fun extractToken(request: HttpServletRequest): Token? {
        val accessToken = extractAccessTokenOrNull(request) ?: return null
        return Token(
            accessToken,
            extractRefreshTokenOrNull(request)
        )
    }

    fun extractAccessTokenOrNull(request: HttpServletRequest): String? {
        return request.getHeader("Authorization")
            .takeIf { it.startsWith("Bearer") }
            ?.replace("Bearer ", "")
    }

    fun extractRefreshTokenOrNull(request: HttpServletRequest): String? {
        return request.getHeader("Refresh")
    }

    private fun parseToken(accessToken: String): Jwt<*, *> {
        return Jwts.parser()
            .verifyWith(jwtProperty.base64UrlSecretKey)
            .build()
            .parse(accessToken)
    }
}

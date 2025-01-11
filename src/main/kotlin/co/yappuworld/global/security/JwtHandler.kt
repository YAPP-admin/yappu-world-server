package co.yappuworld.global.security

import co.yappuworld.global.property.JwtProperty
import io.github.oshai.kotlinlogging.KotlinLogging
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
        return extractAccessTokenOrNull(request)?.let {
            SecurityUser.fromValidToken(parseToken(it) as Map<String, String>)
        }
    }

    fun extractAccessTokenOrNull(request: HttpServletRequest): String? {
        return request.getHeader("Authorization")
            ?.takeIf { it.startsWith("Bearer") }
            ?.replace("Bearer ", "")
    }

    private fun parseToken(accessToken: String): Jwt<*, *> {
        return Jwts.parser()
            .verifyWith(jwtProperty.base64UrlSecretKey)
            .build()
            .parse(accessToken)
    }
}

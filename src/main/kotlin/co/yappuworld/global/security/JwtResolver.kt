package co.yappuworld.global.security

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.property.JwtProperty
import co.yappuworld.global.security.error.TokenError
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.util.UUID

private val logger = KotlinLogging.logger { }

@Component
class JwtResolver(
    private val jwtProperty: JwtProperty
) {

    fun extractSecurityUserOrNull(accessToken: String): SecurityUser? {
        return accessToken.let {
            val payload = parseToken(it).payload
            SecurityUser.fromValidToken(payload as Map<String, String>)
        }
    }

    fun extractUserIdFrom(accessToken: String): UUID {
        return UUID.fromString(getClaimsFrom(accessToken)["userId"].toString())
            ?: throw BusinessException(TokenError.INVALID_TOKEN)
    }

    fun getClaimsFrom(accessToken: String): Map<String, Any> {
        return try {
            parseToken(accessToken).payload as Map<String, Any>
        } catch (e: ExpiredJwtException) {
            e.claims as Map<String, Any>
        }
    }

    private fun parseToken(accessToken: String): Jwt<*, *> {
        return Jwts.parser()
            .verifyWith(jwtProperty.base64UrlSecretKey)
            .build()
            .parse(accessToken)
    }
}

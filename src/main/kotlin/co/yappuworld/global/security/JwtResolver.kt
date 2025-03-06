package co.yappuworld.global.security

import co.yappuworld.global.exception.BusinessException
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

    fun extractSecurityUserOrNull(accessToken: String): SecurityUser? =
        accessToken.let {
            val payload = parseToken(it).payload
            SecurityUser.fromValidToken(payload as Map<*, *>)
        }

    fun extractUserIdFrom(accessToken: String): UUID {
        val userId = getClaimsFrom(accessToken)["userId"]
            ?: throw BusinessException(TokenError.INVALID_TOKEN)
        return UUID.fromString(userId.toString())
    }

    fun getClaimsFrom(accessToken: String): Map<*, *> =
        try {
            parseToken(accessToken).payload as Map<*, *>
        } catch (e: ExpiredJwtException) {
            e.claims as Map<*, *>
        }

    private fun parseToken(accessToken: String): Jwt<*, *> =
        Jwts
            .parser()
            .verifyWith(jwtProperty.base64UrlSecretKey)
            .build()
            .parse(accessToken)
}

package co.yappuworld.global.security

import co.yappuworld.global.vo.UserRole
import java.util.UUID

class SecurityUser(
    private val userId: UUID,
    private val role: UserRole
) {

    val claim: Map<String, String> = mapOf(
        Pair("userId", userId.toString()),
        Pair("role", role.name)
    )

    lateinit var token: Token

    companion object {
        fun fromValidToken(
            token: Token,
            claims: Map<String, String>
        ): SecurityUser? {
            val roleValue = claims["role"] ?: return null
            return SecurityUser(
                UUID.fromString(claims["userId"]) ?: return null,
                UserRole.valueOf(roleValue)
            ).apply { this.token = token }
        }

        fun fromExpiredToken(
            token: Token,
            claims: Map<String, String>
        ): SecurityUser? {
            val roleValue = claims["role"] ?: return null
            return SecurityUser(
                UUID.fromString(claims["userId"]) ?: return null,
                UserRole.valueOf(roleValue)
            ).apply { this.token = token }
        }
    }
}

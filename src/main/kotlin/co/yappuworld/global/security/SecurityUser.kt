package co.yappuworld.global.security

import co.yappuworld.user.domain.UserRole
import co.yappuworld.user.domain.User
import java.util.UUID

class SecurityUser(
    val userId: UUID,
    val role: UserRole
) {

    val claim: Map<String, String> = mapOf(
        Pair("userId", userId.toString()),
        Pair("role", role.name)
    )

    companion object {
        fun fromValidToken(claims: Map<String, String>): SecurityUser? {
            val roleValue = claims["role"] ?: return null
            return SecurityUser(
                UUID.fromString(claims["userId"]) ?: return null,
                UserRole.valueOf(roleValue)
            )
        }

        fun from(user: User): SecurityUser {
            return SecurityUser(user.id, user.role)
        }
    }
}

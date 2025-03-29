package co.yappuworld.global.security

import co.yappuworld.user.domain.model.UserEntity
import co.yappuworld.user.domain.vo.UserRole
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
        fun fromValidToken(claims: Map<*, *>): SecurityUser? {
            if (claims["userId"] == null || claims["role"] == null) {
                return null
            }

            return SecurityUser(
                UUID.fromString(claims["userId"] as String),
                UserRole.valueOf(claims["role"].toString())
            )
        }

        fun from(user: UserEntity): SecurityUser = SecurityUser(user.id, user.role)
    }
}

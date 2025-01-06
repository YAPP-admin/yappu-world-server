package co.yappuworld.user.application.dto.request

import co.yappuworld.global.vo.UserRole
import co.yappuworld.user.domain.User
import co.yappuworld.user.domain.UserSignUpApplication

data class UserSignUpAppRequestDto(
    val email: String,
    val password: String,
    val name: String,
    val activityUnits: List<ActivityUnitAppRequestDto>,
    val signUpCode: String
) {
    fun toUser(role: UserRole): User {
        return User(
            this.email,
            this.password,
            this.name,
            role
        )
    }

    fun toSignUpApplication(): UserSignUpApplication {
        return UserSignUpApplication(
            this.email,
            this.password,
            this.name,
            this.activityUnits.map { it.toDomain() }
        )
    }
}

package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.UserRole
import co.yappuworld.user.domain.User
import co.yappuworld.user.domain.SignUpApplicantDetails

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

    fun toSignUpApplication(): SignUpApplicantDetails {
        return SignUpApplicantDetails(
            this.email,
            this.password,
            this.name,
            this.activityUnits.map { it.toDomain() }
        )
    }
}

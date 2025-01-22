package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.SignUpApplicantDetails
import co.yappuworld.user.domain.model.User
import co.yappuworld.user.domain.vo.UserRole

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
            this.activityUnits.map { it.toActivityUnitParam() }
        )
    }

    fun toActivityUnits(user: User): List<ActivityUnit> {
        return this.activityUnits.map {
            ActivityUnit(
                it.generation,
                it.position.toDomainType(),
                user.id
            )
        }
    }
}

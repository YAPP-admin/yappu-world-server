package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.vo.UserRole

data class SignUpApplicantDetails(
    val email: String,
    val password: String,
    val name: String,
    val activityUnits: List<ActivityUnitParam>
) {
    fun toUser(role: UserRole): User {
        return User(
            this.email,
            this.password,
            this.name,
            role
        )
    }
}

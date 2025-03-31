package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.vo.UserRole

data class ApplicationDetails(
    val email: String,
    val password: String,
    val name: String,
    val activityUnits: List<ActivityUnitParam>,
    val fcmToken: String,
    val deviceAlarmToggle: Boolean
) {

    fun toUser(role: UserRole): UserEntity =
        UserEntity(
            this.email,
            this.password,
            this.name,
            role
        )
}

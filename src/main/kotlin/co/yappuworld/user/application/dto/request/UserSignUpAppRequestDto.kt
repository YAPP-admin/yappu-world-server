package co.yappuworld.user.application.dto.request

import co.yappuworld.user.domain.model.ApplicationDetails
import co.yappuworld.user.domain.model.SignUpApplication

data class UserSignUpAppRequestDto(
    val email: String,
    val password: String,
    val name: String,
    val activityUnits: List<ActivityUnitAppRequestDto>,
    val signUpCode: String?,
    val fcmToken: String,
    val deviceAlarmToggle: Boolean
) {

    fun toApplication(): SignUpApplication {
        return SignUpApplication(
            ApplicationDetails(
                this.email,
                this.password,
                this.name,
                this.activityUnits.map { it.toActivityUnitParam() },
                this.fcmToken,
                this.deviceAlarmToggle
            )
        )
    }
}

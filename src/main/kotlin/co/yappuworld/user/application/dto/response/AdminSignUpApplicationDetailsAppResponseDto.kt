package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.SignUpApplication
import java.time.LocalDate

data class AdminSignUpApplicationDetailsAppResponseDto(
    val name: String,
    val email: String,
    val applicationDate: LocalDate,
    val activityUnits: List<AdminSignUpApplicationActivityUnitAppResponseDto>
) {

    constructor(application: SignUpApplication) : this(
        name = application.details.name,
        email = application.details.email,
        applicationDate = application.createdAt.toLocalDate(),
        activityUnits = application.details.activityUnits.map { AdminSignUpApplicationActivityUnitAppResponseDto(it) }
    )
}

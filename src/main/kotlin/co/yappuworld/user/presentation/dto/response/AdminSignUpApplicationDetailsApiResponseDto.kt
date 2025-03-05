package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.AdminSignUpApplicationDetailsAppResponseDto
import java.time.LocalDate

data class AdminSignUpApplicationDetailsApiResponseDto(
    val name: String,
    val email: String,
    val applicationDate: LocalDate,
    val activityUnits: List<AdminSignUpApplicationActivityUnitApiResponseDto>
) {

    constructor(response: AdminSignUpApplicationDetailsAppResponseDto) : this(
        name = response.name,
        email = response.email,
        applicationDate = response.applicationDate,
        activityUnits = response.activityUnits.map { AdminSignUpApplicationActivityUnitApiResponseDto(it) }
    )
}

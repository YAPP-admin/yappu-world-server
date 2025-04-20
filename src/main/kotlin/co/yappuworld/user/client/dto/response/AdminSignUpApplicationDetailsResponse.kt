package co.yappuworld.user.client.dto.response

import co.yappuworld.user.domain.entity.SignUpApplicationEntity
import java.time.LocalDate

data class AdminSignUpApplicationDetailsResponse(
    val name: String,
    val email: String,
    val applicationDate: LocalDate,
    val activityUnits: List<AdminSignUpApplicationActivityUnitResponse>
) {

    constructor(application: SignUpApplicationEntity) : this(
        name = application.details.name,
        email = application.details.email,
        applicationDate = application.createdAt.toLocalDate(),
        activityUnits = application.details.activityUnits.map { AdminSignUpApplicationActivityUnitResponse(it) }
    )
}

package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.SignUpApplication
import org.springframework.data.domain.Page

data class AdminSignUpApplicationBundleAppResponse(
    val data: List<AdminSignUpApplicationOverviewAppResponseDto>,
    val totalCount: Long
) {

    constructor(page: Page<SignUpApplication>) : this(
        data = page.content.map { AdminSignUpApplicationOverviewAppResponseDto(it) },
        totalCount = page.totalElements
    )
}

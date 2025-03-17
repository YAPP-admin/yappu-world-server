package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.UserActivityHistoriesAppResponseDto
import co.yappuworld.user.application.dto.response.UserActivityHistoryAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class UserActivityHistoriesApiResponseDto(
    val activityUnits: List<UserActivityHistoryApiResponseDto>
) {

    constructor(response: UserActivityHistoriesAppResponseDto) : this(
        response.activityUnits.map { UserActivityHistoryApiResponseDto(it) }
    )
}

data class UserActivityHistoryApiResponseDto(
    @Schema(description = "기수")
    val generation: Int,
    @Schema(
        description = "직군",
        allowableValues = ["PM", "Design", "Web", "Android", "iOS", "Flutter", "Server", "Staff"]
    )
    val position: String,
    @Schema(description = "활동 시작일", nullable = true)
    val activityStartDate: LocalDate?,
    @Schema(description = "활동 종료일", nullable = true)
    val activityEndDate: LocalDate?
) {
    constructor(response: UserActivityHistoryAppResponseDto) : this(
        generation = response.generation,
        position = response.position,
        activityStartDate = response.activityStartDate,
        activityEndDate = response.activityEndDate
    )
}

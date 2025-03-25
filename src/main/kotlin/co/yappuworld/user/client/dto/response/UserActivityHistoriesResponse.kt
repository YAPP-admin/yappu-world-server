package co.yappuworld.user.client.dto.response

import co.yappuworld.operation.domain.GenerationEntity
import co.yappuworld.user.domain.model.ActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class UserActivityHistoriesResponse(
    val activityUnits: List<UserActivityHistoryResponse>
) {

    constructor(activityUnits: List<ActivityUnit>, generationByValue: Map<Int, GenerationEntity>) : this(
        activityUnits
            .sortedByDescending { it.generation }
            .map {
                UserActivityHistoryResponse(
                    it,
                    generationByValue[it.generation]
                )
            }
    )
}

data class UserActivityHistoryResponse(
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

    constructor(activityUnit: ActivityUnit, generation: GenerationEntity?) : this(
        generation = activityUnit.generation,
        position = activityUnit.position.label,
        activityStartDate = generation?.startDate,
        activityEndDate = generation?.endDate
    )
}

package co.yappuworld.user.application.dto.response

import co.yappuworld.operation.domain.Generation
import co.yappuworld.user.domain.model.ActivityUnit
import java.time.LocalDate

data class UserActivityHistoriesAppResponseDto(
    val activityUnits: List<UserActivityHistoryAppResponseDto>
) {

    companion object {
        fun from(
            activityUnits: List<ActivityUnit>,
            generations: Map<Int, Generation>
        ): UserActivityHistoriesAppResponseDto =
            UserActivityHistoriesAppResponseDto(
                activityUnits.map {
                    UserActivityHistoryAppResponseDto(
                        it,
                        generations[it.generation]
                    )
                }
            )
    }
}

data class UserActivityHistoryAppResponseDto(
    val generation: Int,
    val position: String,
    val activityStartDate: LocalDate?,
    val activityEndDate: LocalDate?
) {

    constructor(
        activityUnit: ActivityUnit,
        generation: Generation?
    ) : this(
        generation = activityUnit.generation,
        position = activityUnit.position.label,
        activityStartDate = generation?.startDate,
        activityEndDate = generation?.endDate
    )
}

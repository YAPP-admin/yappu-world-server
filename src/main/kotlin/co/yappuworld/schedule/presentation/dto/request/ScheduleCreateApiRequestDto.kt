package co.yappuworld.schedule.presentation.dto.request

import co.yappuworld.schedule.domain.ScheduleType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleCreateApiRequestDto(
    @Schema(description = "스케줄 이름", nullable = false)
    @field:NotBlank
    val name: String,
    @Schema(description = "설명", nullable = true)
    val description: String?,
    @Schema(description = "장소", nullable = true)
    val place: String?,
    @Schema(description = "날짜/시작일(종료일이 있는 경우)", nullable = false)
    val date: LocalDate,
    @Schema(description = "종료일", nullable = true)
    var endDate: LocalDate?,
    @Schema(description = "시간/시작 시간(종료 시간이 있는 경우)", nullable = true, example = "14:00:00")
    var time: LocalTime?,
    @Schema(description = "종료 시간", nullable = true, example = "18:00:00")
    var endTime: LocalTime?,
    @Schema(description = "스케줄 종류", nullable = false)
    @field:NotNull
    var type: ScheduleType
)

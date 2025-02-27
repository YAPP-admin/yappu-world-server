package co.yappuworld.schedule.presentation.dto.request

import co.yappuworld.schedule.application.dto.request.ScheduleCreateAppRequestDto
import co.yappuworld.schedule.domain.ScheduleType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleCreateApiRequestDto(
    @Schema(description = "스케줄 이름", nullable = false, example = "데모데이")
    @field:NotBlank
    val name: String,
    @Schema(description = "설명", nullable = true, example = "데모데이는 아주 거창한 행사에요")
    val description: String?,
    @Schema(description = "장소", nullable = true, example = "공덕 창업 허브")
    val place: String?,
    @Schema(description = "날짜/시작일(종료일이 있는 경우)", nullable = false, example = "2025-02-27")
    val date: LocalDate,
    @Schema(description = "종료일", nullable = true, example = "2025-02-27")
    var endDate: LocalDate?,
    @Schema(description = "시간/시작 시간(종료 시간이 있는 경우)", nullable = true, example = "14:00:00")
    var time: LocalTime?,
    @Schema(description = "종료 시간", nullable = true, example = "18:00:00")
    var endTime: LocalTime?,
    @Schema(description = "스케줄 종류", nullable = false, example = "SESSION")
    @field:NotNull
    var type: ScheduleType
) {

    fun toAppRequest(): ScheduleCreateAppRequestDto {
        return ScheduleCreateAppRequestDto(
            name = this.name,
            description = this.description,
            place = this.place,
            date = this.date,
            endDate = this.endDate,
            time = this.time,
            endTime = this.endTime,
            type = this.type
        )
    }
}

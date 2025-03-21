package co.yappuworld.schedule.application.dto.request

import co.yappuworld.schedule.domain.ScheduleEntity
import co.yappuworld.schedule.domain.ScheduleType
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime

data class AdminSessionCreateRequest(
    @Schema(description = "스케줄 이름", nullable = false, example = "데모데이")
    @field:NotBlank
    val name: String,
    @Schema(description = "설명", nullable = true, example = "데모데이는 아주 거창한 행사에요")
    val description: String? = null,
    @Schema(description = "장소", nullable = true, example = "공덕 창업 허브")
    val place: String? = null,
    @Schema(description = "시작일", nullable = false, example = "2025-02-27")
    val date: LocalDate,
    @Schema(description = "종료일", nullable = false, example = "2025-02-27")
    var endDate: LocalDate,
    @Schema(description = "시작 시간, 하루 종일이라면 null", nullable = true, example = "14:00:00")
    var time: LocalTime? = null,
    @Schema(description = "종료 시간, 하루 종일이라면 null", nullable = true, example = "18:00:00")
    var endTime: LocalTime? = null,
    @Schema(
        description = """
            하루 종일인지 여부
            시작과 종료 시간이 있다면 false
            시작 시간, 종료 시간보다 우선함        
        """,
        nullable = true
    )
    val isAllDay: Boolean = false,
    @Schema(description = "기수(세션, 태스크의 경우 필수)", nullable = false, example = "25")
    var generation: Int,
    @Schema(description = "스케줄 종류", nullable = false, example = "SESSION")
    @field:NotNull
    var type: ScheduleType,
    @Schema(description = "세션 종류", nullable = false, example = "OFFLINE")
    var sessionType: SessionType
) {

    init {
        if (isAllDay) {
            this.time = null
            this.endTime = null
        }
    }

    fun toDomain(): ScheduleEntity =
        when (type) {
            ScheduleType.SESSION -> convertToSession()
            ScheduleType.TASK -> TODO()
            ScheduleType.ETC -> TODO()
        }

    private fun convertToSession(): ScheduleEntity =
        SessionEntity(
            name = name,
            description = description,
            place = place,
            date = date,
            endDate = endDate,
            time = time,
            endTime = endTime,
            isAllDay = isAllDay,
            generation = generation,
            sessionType = sessionType
        )
}

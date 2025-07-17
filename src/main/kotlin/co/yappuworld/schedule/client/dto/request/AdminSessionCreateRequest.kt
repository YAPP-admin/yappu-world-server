package co.yappuworld.schedule.client.dto.request

import co.yappuworld.schedule.domain.vo.ScheduleType
import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.entity.ScheduleEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

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
    @Schema(description = "시작 시간", nullable = false, example = "14:00:00", type = "string")
    var time: LocalTime,
    @Schema(description = "종료 시간", nullable = false, example = "18:00:00", type = "string")
    var endTime: LocalTime,
    @Schema(description = "기수(세션, 태스크의 경우 필수)", nullable = false, example = "25")
    var generation: Int,
    @Schema(description = "스케줄 종류", nullable = false, example = "SESSION")
    @field:NotNull
    var type: ScheduleType,
    @Schema(description = "세션 종류", nullable = false, example = "OFFLINE")
    var sessionType: SessionType,
    @Schema(description = "세션 참석자 ID", nullable = false)
    val sessionAttendeeIds: List<UUID>,
    @Schema(description = "세션 공지사항 ID", nullable = false)
    val noticeIds: List<UUID>
) {

    fun toDomain(): ScheduleEntity =
        SessionEntity(
            name = name,
            description = description,
            place = place,
            date = date,
            endDate = endDate,
            time = time,
            endTime = endTime,
            isAllDay = false,
            generation = generation,
            sessionType = sessionType
        )
}

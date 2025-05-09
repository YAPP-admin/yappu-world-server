package co.yappuworld.schedule.client.dto.request

import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionUpdateRequest(
    @Schema(description = "세션 ID")
    val id: UUID,
    @Schema(description = "세션 이름", example = "데모데이")
    val name: String,
    @Schema(description = "기수", example = "25")
    val generation: Int,
    @Schema(description = "장소", example = "마루 180")
    val place: String?,
    @Schema(description = "세션 시작일", example = "2025-02-25")
    val date: LocalDate,
    @Schema(description = "세션 종료일", example = "2025-02-25")
    val endDate: LocalDate,
    @Schema(description = "시작 시간", example = "14:00:00", type = "string")
    val time: LocalTime,
    @Schema(description = "종료 시간", example = "17:00:00", type = "string")
    val endTime: LocalTime,
    @Schema(description = "세션 종류", example = "OFFLINE")
    val sessionType: SessionType
) {

    fun applyTo(session: SessionEntity) {
        session.update(
            name = name,
            description = null,
            generation = generation,
            place = place,
            date = date,
            endDate = endDate,
            time = time,
            endTime = endTime,
            sessionType = sessionType
        )
    }
}

package co.yappuworld.schedule.client.dto.request

import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionUpdateRequest(
    @field:Schema(description = "세션 ID")
    val id: UUID,
    @field:Schema(description = "세션 이름", example = "데모데이")
    val name: String,
    @field:Schema(description = "기수", example = "25")
    val generation: Int,
    @field:Schema(description = "장소", example = "마루 180")
    val place: String?,
    @param:Schema(description = "주소", nullable = false, example = "서울특별시 종로구 종로 33")
    val address: String? = null,
    @param:Schema(description = "경도", nullable = false, example = "126.981437983842")
    val longitude: Double? = null,
    @param:Schema(description = "위도", nullable = false, example = "37.5720065838703")
    val latitude: Double? = null,
    @field:Schema(description = "세션 시작일", example = "2025-02-25")
    val date: LocalDate,
    @field:Schema(description = "세션 종료일", example = "2025-02-25")
    val endDate: LocalDate,
    @field:Schema(description = "시작 시간", example = "14:00:00", type = "string")
    val time: LocalTime,
    @field:Schema(description = "종료 시간", example = "17:00:00", type = "string")
    val endTime: LocalTime,
    @field:Schema(description = "세션 종류", example = "OFFLINE")
    val sessionType: SessionType,
    @field:Schema(description = "세션 참석자 ID", nullable = false)
    val sessionAttendeeIds: List<UUID>,
    @field:Schema(description = "세션 공지사항 ID", nullable = false)
    val noticeIds: List<UUID>
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

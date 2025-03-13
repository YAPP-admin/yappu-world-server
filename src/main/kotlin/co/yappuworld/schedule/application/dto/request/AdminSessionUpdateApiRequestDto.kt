package co.yappuworld.schedule.application.dto.request

import co.yappuworld.schedule.domain.SessionType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionUpdateApiRequestDto(
    val id: UUID,
    val name: String,
    val generation: Int,
    val place: String?,
    val date: LocalDate,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val sessionType: SessionType
)

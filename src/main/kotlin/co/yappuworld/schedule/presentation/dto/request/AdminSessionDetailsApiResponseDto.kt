package co.yappuworld.schedule.presentation.dto.request

import co.yappuworld.schedule.application.dto.response.AdminSessionDetailsAppResponseDto
import co.yappuworld.schedule.domain.SessionType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionDetailsApiResponseDto(
    val id: UUID,
    val name: String,
    val generation: Int,
    val place: String?,
    val date: LocalDate,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val sessionType: SessionType
) {

    constructor(session: AdminSessionDetailsAppResponseDto) : this(
        id = session.id,
        name = session.name,
        generation = session.generation,
        place = session.place,
        date = session.date,
        time = session.time,
        endTime = session.endTime,
        sessionType = session.sessionType
    )
}

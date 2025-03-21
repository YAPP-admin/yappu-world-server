package co.yappuworld.schedule.application.dto.request

import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionUpdateAppRequestDto(
    val id: UUID,
    val name: String,
    val generation: Int,
    val place: String?,
    val date: LocalDate,
    val endDate: LocalDate,
    val time: LocalTime?,
    val endTime: LocalTime?,
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

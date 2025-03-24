package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionDetailResponse(
    val id: UUID,
    val name: String,
    val generation: Int,
    val place: String?,
    val date: LocalDate,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val sessionType: SessionType
) {

    constructor(session: SessionEntity) : this(
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

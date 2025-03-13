package co.yappuworld.schedule.application.dto.response

import co.yappuworld.schedule.domain.ScheduleEntity
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.domain.SessionType
import org.springframework.data.domain.Page
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionPageAppResponseDto(
    val sessions: List<AdminSessionOverviewAppResponseDto>,
    val totalElements: Long,
    val totalPages: Int
) {

    constructor(page: Page<out ScheduleEntity>) : this(
        sessions = page.content.map { AdminSessionOverviewAppResponseDto(it as SessionEntity) },
        totalElements = page.totalElements,
        totalPages = page.totalPages
    )
}

data class AdminSessionOverviewAppResponseDto(
    val id: UUID,
    val generation: Int,
    val type: SessionType,
    val name: String,
    val place: String?,
    val date: LocalDate,
    val time: LocalTime?,
    val endTime: LocalTime?
) {

    constructor(session: SessionEntity) : this(
        id = session.id,
        generation = session.generation,
        type = session.sessionType,
        name = session.name,
        place = session.place,
        date = session.date,
        time = session.time,
        endTime = session.endTime
    )
}

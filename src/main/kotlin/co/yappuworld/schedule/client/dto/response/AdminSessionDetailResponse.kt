package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.dto.SessionAttendeeDto
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.domain.vo.Position
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionDetailResponse(
    @Schema(description = "세션 ID")
    val id: UUID,
    @Schema(description = "세션 이름")
    val name: String,
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "세션 장소")
    val place: String?,
    @Schema(description = "세션 시작 일자")
    val date: LocalDate,
    @Schema(description = "세션 종료 일자")
    val endDate: LocalDate,
    @Schema(description = "세션 시작 시간")
    val time: LocalTime,
    @Schema(description = "세션 종료 시간")
    val endTime: LocalTime,
    @Schema(description = "세션 타입")
    val sessionType: SessionType,
    val attendees: List<AdminSessionAttendeeByPositionResponse>
) {

    constructor(session: SessionEntity, attendees: List<SessionAttendeeDto>) : this(
        id = session.id,
        name = session.name,
        generation = session.generation,
        place = session.place,
        date = session.date,
        endDate = session.endDate,
        time = session.time,
        endTime = session.endTime,
        sessionType = session.sessionType,
        attendees = attendees
            .groupBy { it.position }
            .let { attendeesGroupByPosition ->
                Position.activeUserPositions.map { position ->
                    AdminSessionAttendeeByPositionResponse(
                        position = position,
                        attendees = attendeesGroupByPosition[position]?.map { AdminSessionAttendeeResponse(it) }
                            ?: emptyList()
                    )
                }
            }.sortedBy { it.position.ordinal }
    )
}

data class AdminSessionAttendeeByPositionResponse(
    val position: Position,
    val attendees: List<AdminSessionAttendeeResponse>
)

data class AdminSessionAttendeeResponse(
    val userId: UUID,
    val name: String,
    val position: Position
) {

    constructor(attendee: SessionAttendeeDto) : this(
        userId = attendee.userId,
        name = attendee.name,
        position = attendee.position
    )
}

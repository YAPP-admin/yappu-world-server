package co.yappuworld.schedule.client.dto.response

import co.yappuworld.post.infrastructure.entity.NoticeEntity
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
    @Schema(description = "세션 참가자 목록")
    val attendees: List<AdminSessionAttendeeByPositionResponse>,
    @Schema(description = "세션 공지사항 목록")
    val notices: List<AdminSessionNoticeResponse>
) {

    companion object {

        fun from(
            session: SessionEntity,
            attendees: List<SessionAttendeeDto>,
            notices: List<NoticeEntity>
        ): AdminSessionDetailResponse =
            AdminSessionDetailResponse(
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
                    }.sortedBy { it.position.ordinal },
                notices = notices.map { n -> AdminSessionNoticeResponse(n.id, n.title) }
            )
    }
}

data class AdminSessionAttendeeByPositionResponse(
    @Schema(description = "참석자 직군")
    val position: Position,
    @Schema(description = "직군 내 참석자 목록")
    val attendees: List<AdminSessionAttendeeResponse>
)

data class AdminSessionAttendeeResponse(
    @Schema(description = "참석자 ID")
    val userId: UUID,
    @Schema(description = "참석자 이름")
    val name: String,
    @Schema(description = "참석자 직군")
    val position: Position
) {

    constructor(attendee: SessionAttendeeDto) : this(
        userId = attendee.userId,
        name = attendee.name,
        position = attendee.position
    )
}

data class AdminSessionNoticeResponse(
    @Schema(description = "공지사항 ID")
    val noticeId: UUID,
    @Schema(description = "공지사항 제목")
    val title: String
)

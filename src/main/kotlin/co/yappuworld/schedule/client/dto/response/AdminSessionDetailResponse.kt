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
    @field:Schema(description = "세션 ID")
    val id: UUID,
    @field:Schema(description = "세션 이름")
    val name: String,
    @field:Schema(description = "기수")
    val generation: Int,
    @field:Schema(description = "세션 장소")
    val place: String?,
    @field:Schema(description = "주소", example = "서울특별시 종로구 종로 33")
    val address: String?,
    @field:Schema(description = "경도", example = "126.981437983842")
    val longitude: Double?,
    @field:Schema(description = "위도", example = "37.5720065838703")
    val latitude: Double?,
    @field:Schema(description = "세션 시작 일자")
    val date: LocalDate,
    @field:Schema(description = "세션 종료 일자")
    val endDate: LocalDate,
    @field:Schema(description = "세션 시작 시간")
    val time: LocalTime,
    @field:Schema(description = "세션 종료 시간")
    val endTime: LocalTime,
    @field:Schema(description = "세션 타입")
    val sessionType: SessionType,
    @field:Schema(description = "세션 참가자 목록")
    val attendees: List<AdminSessionAttendeeByPositionResponse>,
    @field:Schema(description = "세션 공지사항 목록")
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
                address = session.address,
                latitude = session.latitude,
                longitude = session.longitude,
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
    @field:Schema(description = "참석자 직군")
    val position: Position,
    @field:Schema(description = "직군 내 참석자 목록")
    val attendees: List<AdminSessionAttendeeResponse>
)

data class AdminSessionAttendeeResponse(
    @field:Schema(description = "참석자 ID")
    val userId: UUID,
    @field:Schema(description = "참석자 이름")
    val name: String,
    @field:Schema(description = "참석자 직군")
    val position: Position
) {

    constructor(attendee: SessionAttendeeDto) : this(
        userId = attendee.userId,
        name = attendee.name,
        position = attendee.position
    )
}

data class AdminSessionNoticeResponse(
    @field:Schema(description = "공지사항 ID")
    val noticeId: UUID,
    @field:Schema(description = "공지사항 제목")
    val title: String
)

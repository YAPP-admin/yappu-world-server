package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.client.dto.response.PositionResponse
import co.yappuworld.post.domain.NoticeType
import co.yappuworld.post.infrastructure.entity.NoticeEntity
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.domain.vo.SessionProgressPhase
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class SessionDetailsResponse(
    @field:Schema(description = "세션 ID")
    val id: UUID,
    @field:Schema(description = "세션 진행 단계", example = "ONGOING")
    val progressPhase: SessionProgressPhase,
    @field:Schema(description = "세션 제목", example = "개발 세션")
    val title: String,
    @field:Schema(description = "시작 날짜", example = "2024-01-01")
    val startDate: LocalDate,
    @field:Schema(description = "시작 시간", example = "09:00:00")
    val startTime: LocalTime,
    @field:Schema(description = "시작 요일", example = "월")
    val startDayOfWeek: String,
    @field:Schema(description = "종료 날짜", example = "2024-12-31")
    val endDate: LocalDate,
    @field:Schema(description = "종료 시간", example = "18:00:00")
    val endTime: LocalTime,
    @field:Schema(description = "종료 요일", example = "토")
    val endDayOfWeek: String,
    @field:Schema(description = "장소 이름", nullable = true)
    val place: String?,
    @field:Schema(description = "주소", nullable = true)
    val address: String?,
    @field:Schema(description = "위도", nullable = true)
    val latitude: Double?,
    @field:Schema(description = "경도", nullable = true)
    val longitude: Double?,
    @field:Schema(description = "공지사항 목록")
    val notices: List<SessionDetailsNoticeOverviewResponse>
) {
    companion object {
        fun of(
            session: SessionEntity,
            notices: List<NoticeEntity>,
            writers: Map<UUID, UserWithLastActivityUnit>,
            now: LocalDateTime
        ): SessionDetailsResponse =
            SessionDetailsResponse(
                id = session.id,
                progressPhase = session.getSessionProgressPhase(now),
                title = session.name,
                startDate = session.date,
                startTime = session.time,
                startDayOfWeek = session.startDayOfWeek,
                endDate = session.endDate,
                endTime = session.endTime,
                endDayOfWeek = session.endDayOfWeek,
                place = session.place,
                address = session.address,
                latitude = session.latitude,
                longitude = session.longitude,
                notices = notices.map { notice ->
                    val writer = writers[notice.writerId]
                        ?: throw BusinessException(ScheduleError.NO_WRITER_FOR_SCHEDULE)
                    SessionDetailsNoticeOverviewResponse.of(notice, writer)
                }
            )
    }
}

data class SessionDetailsNoticeOverviewResponse(
    val notice: SessionDetailsNoticeResponse,
    val writer: SessionDetailsNoticeWriterResponse
) {
    companion object {
        fun of(
            notice: NoticeEntity,
            writer: UserWithLastActivityUnit
        ) = SessionDetailsNoticeOverviewResponse(
            notice = SessionDetailsNoticeResponse.from(notice),
            writer = SessionDetailsNoticeWriterResponse.from(writer)
        )
    }
}

data class SessionDetailsNoticeResponse(
    @field:Schema(description = "공지사항 ID")
    val id: UUID,
    @field:Schema(description = "작성일")
    val createdAt: LocalDate,
    @field:Schema(description = "공지사항 제목")
    val title: String,
    @field:Schema(description = "공지사항 내용 (최대 200자)")
    val content: String,
    @field:Schema(description = "공지사항 종류")
    val noticeType: NoticeType
) {

    companion object {
        fun from(notice: NoticeEntity) =
            SessionDetailsNoticeResponse(
                id = notice.id,
                createdAt = notice.createdAt.toLocalDate(),
                title = notice.title,
                content = notice.contentSummary.take(200),
                noticeType = notice.noticeType
            )
    }
}

data class SessionDetailsNoticeWriterResponse(
    @field:Schema(description = "작성자 ID")
    val id: UUID,
    @field:Schema(description = "작성자 이름")
    val name: String,
    @field:Schema(description = "작성자 가장 최근 활동 기수")
    val activityUnitGeneration: Int,
    @field:Schema(description = "작성자 직군")
    val activityUnitPosition: PositionResponse
) {

    companion object {
        fun from(writer: UserWithLastActivityUnit) =
            SessionDetailsNoticeWriterResponse(
                id = writer.userId,
                name = writer.name,
                activityUnitGeneration = writer.lastActiveGeneration,
                activityUnitPosition = PositionResponse(writer.lastActivePosition)
            )
    }
}

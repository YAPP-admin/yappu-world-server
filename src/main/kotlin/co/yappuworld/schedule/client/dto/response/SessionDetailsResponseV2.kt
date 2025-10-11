package co.yappuworld.schedule.client.dto.response

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.post.infrastructure.entity.NoticeEntity
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.domain.vo.SessionProgressPhase
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

data class SessionDetailsResponseV2(
    @field:Schema(description = "세션 ID")
    val id: UUID,
    @field:Schema(description = "세션 진행 단계", example = "ONGOING")
    val progressPhase: SessionProgressPhase,
    @field:Schema(description = "세션 제목", example = "개발 세션")
    val title: String,
    @field:Schema(description = "세션 시작 일시", example = "2024-01-01T09:00:00+09:00")
    val startDateTime: LocalDateTime,
    @field:Schema(description = "시작 요일", example = "월")
    val startDayOfWeek: String,
    @field:Schema(description = "세션 종료 일시", example = "2024-12-31T18:00:00+09:00")
    val endDateTime: LocalDateTime,
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
        ): SessionDetailsResponseV2 =
            SessionDetailsResponseV2(
                id = session.id,
                progressPhase = session.getSessionProgressPhase(now),
                title = session.name,
                startDateTime = LocalDateTime.of(session.date, session.time),
                startDayOfWeek = session.startDayOfWeek,
                endDateTime = LocalDateTime.of(session.endDate, session.endTime),
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

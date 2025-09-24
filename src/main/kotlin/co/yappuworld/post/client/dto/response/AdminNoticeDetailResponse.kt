package co.yappuworld.post.client.dto.response

import co.yappuworld.post.domain.NoticeType
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminNoticeDetailResponse(
    @Schema(description = "공지사항 ID")
    val noticeId: UUID,
    @Schema(description = "공지사항 작성일")
    val createdAt: String,
    @Schema(description = "공지사항 제목")
    val title: String,
    @Schema(description = "공지사항 내용")
    val content: String,
    @Schema(description = "공지사항 타입")
    val type: NoticeType,
    @Schema(description = "공지사항 작성자")
    val writer: AdminNoticeDetailWriterResponse,
    @Schema(description = "공지사항의 대상 세션", nullable = true)
    val targetSession: AdminNoticeTargetedSessionResponse?
)

data class AdminNoticeDetailWriterResponse(
    val id: UUID,
    val name: String
) {
    constructor(writer: UserEntity) : this(
        id = writer.id,
        name = writer.name
    )
}

data class AdminNoticeTargetedSessionResponse(
    @Schema(description = "세션 ID")
    val sessionId: UUID,
    @Schema(description = "세션 제목")
    val title: String,
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "시작일")
    val date: LocalDate,
    @Schema(description = "시작 시간", nullable = false, example = "18:00:00", type = "string")
    val time: LocalTime
) {
    constructor(session: SessionEntity) : this(
        sessionId = session.id,
        title = session.name,
        generation = session.generation,
        date = session.date,
        time = session.time
    )
}

package co.yappuworld.post.client.dto.response

import co.yappuworld.post.domain.NoticeEntity
import co.yappuworld.user.domain.entity.UserEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

data class AdminNoticeSummaryResponse(
    @Schema(description = "공지사항 ID")
    val noticeId: String,
    @Schema(description = "공지사항 제목")
    val title: String,
    @Schema(description = "공지사항 생성일")
    val createdAt: LocalDateTime,
    @Schema(description = "공지사항 작성자")
    val writer: AdminNoticeSummaryWriterResponse,
    @Schema(description = "공지사항 타입")
    val noticeType: String
) {
    constructor(notice: NoticeEntity, user: UserEntity) : this(
        noticeId = notice.id.toString(),
        title = notice.title,
        createdAt = notice.createdAt,
        writer = AdminNoticeSummaryWriterResponse(user),
        noticeType = notice.noticeType.label
    )
}

data class AdminNoticeSummaryWriterResponse(
    @Schema(description = "작성자 ID")
    val userId: UUID,
    @Schema(description = "작성자 이름")
    val name: String
) {
    constructor(user: UserEntity) : this(user.id, user.name)
}

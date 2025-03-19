package co.yappuworld.post.client.dto.response

import co.yappuworld.post.domain.model.NoticeEntity
import co.yappuworld.user.domain.model.User
import java.time.LocalDateTime
import java.util.UUID

data class AdminNoticeSummaryResponse(
    val noticeId: String,
    val title: String,
    val createdAt: LocalDateTime,
    val writer: AdminNoticeSummaryWriterResponse,
    val noticeType: String
) {
    constructor(notice: NoticeEntity, user: User) : this(
        noticeId = notice.id.toString(),
        title = notice.title,
        createdAt = notice.createdAt,
        writer = AdminNoticeSummaryWriterResponse(user),
        noticeType = notice.noticeType.label
    )
}

data class AdminNoticeSummaryWriterResponse(
    val userId: UUID,
    val name: String
) {
    constructor(user: User) : this(user.id, user.name)
}

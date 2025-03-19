package co.yappuworld.post.client.dto.response

import co.yappuworld.post.domain.vo.NoticeType
import co.yappuworld.user.domain.model.User
import java.util.UUID

data class AdminNoticeDetailResponse(
    val noticeId: UUID,
    val createdAt: String,
    val title: String,
    val content: String,
    val type: NoticeType,
    val writer: AdminNoticeDetailWriterResponse
)

data class AdminNoticeDetailWriterResponse(
    val id: UUID,
    val name: String
) {
    constructor(writer: User) : this(
        id = writer.id,
        name = writer.name
    )
}

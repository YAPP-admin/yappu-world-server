package co.yappuworld.post.client.dto.response

import co.yappuworld.post.domain.NoticeType
import co.yappuworld.user.domain.model.UserEntity
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
    constructor(writer: UserEntity) : this(
        id = writer.id,
        name = writer.name
    )
}

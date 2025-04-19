package co.yappuworld.post.client.dto.response

import co.yappuworld.post.domain.NoticeType
import co.yappuworld.user.domain.model.UserEntity
import io.swagger.v3.oas.annotations.media.Schema
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

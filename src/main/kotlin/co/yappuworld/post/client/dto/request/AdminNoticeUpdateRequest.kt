package co.yappuworld.post.client.dto.request

import co.yappuworld.post.domain.NoticeType
import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.Length
import java.util.UUID

data class AdminNoticeUpdateRequest(
    @Schema(description = "공지사항 ID")
    val id: UUID,
    @Schema(description = "공지사항 타입")
    val type: NoticeType,
    @Schema(description = "공지사항 제목")
    @field:Length(max = 50, message = "제목은 50자 이하여야 합니다.")
    val title: String,
    @Schema(description = "마크다운 형태 내용")
    @field:Length(max = 5000, message = "내용은 5000자 이하여야 합니다.")
    val content: String,
    @Schema(description = "평문 형태 내용")
    val plainContent: String,
    @Schema(description = "공지사항이 대상으로 하는 세션", nullable = true)
    val sessionId: UUID? = null
)

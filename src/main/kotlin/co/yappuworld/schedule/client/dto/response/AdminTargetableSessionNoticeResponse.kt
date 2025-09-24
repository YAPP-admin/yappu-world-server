package co.yappuworld.schedule.client.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

data class AdminTargetableSessionNoticeResponse(
    @field:Schema(description = "공지 ID")
    val id: UUID,
    @field:Schema(description = "공지 제목")
    val title: String,
    @field:Schema(description = "공지 작성일시")
    val createdAt: LocalDateTime,
    @field:Schema(description = "다른 세션에서 선택한 공지사항인지 여부")
    val isSelectedByOtherSession: Boolean
)

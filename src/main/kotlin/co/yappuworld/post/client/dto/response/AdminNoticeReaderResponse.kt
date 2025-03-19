package co.yappuworld.post.client.dto.response

import co.yappuworld.user.domain.vo.Position
import java.util.UUID

data class AdminNoticeReaderResponse(
    val id: UUID,
    val name: String,
    val activityUnit: NoticeReaderActivityUnitResponse
)

data class NoticeReaderActivityUnitResponse(
    val generation: Int,
    val position: Position
)

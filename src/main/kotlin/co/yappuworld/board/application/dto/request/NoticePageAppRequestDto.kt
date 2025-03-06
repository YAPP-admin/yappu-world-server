package co.yappuworld.board.application.dto.request

import co.yappuworld.board.domain.vo.NoticeType
import java.util.UUID

data class NoticePageAppRequestDto(
    val lastBoardId: UUID?,
    val limit: Int,
    val noticeType: NoticeType?
)

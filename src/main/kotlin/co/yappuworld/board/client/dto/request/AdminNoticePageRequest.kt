package co.yappuworld.board.client.dto.request

import co.yappuworld.board.domain.vo.NoticeType
import org.springframework.data.domain.PageRequest

data class AdminNoticePageRequest(
    val page: Int,
    val size: Int,
    val noticeType: NoticeType
) {

    fun toPageRequest(): PageRequest = PageRequest.of(page - 1, size)
}

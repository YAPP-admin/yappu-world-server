package co.yappuworld.board.client.application

import co.yappuworld.board.client.dto.request.AdminNoticePageRequest
import co.yappuworld.board.infrastructure.NoticeRepository
import org.springframework.stereotype.Service

@Service
class AdminNoticeService(
    private val noticeRepository: NoticeRepository
) {

    fun getNotices(request: AdminNoticePageRequest) {
        TODO()
    }
}

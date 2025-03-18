package co.yappuworld.board.client.presentation

import co.yappuworld.board.client.application.AdminNoticeService
import co.yappuworld.board.client.dto.request.AdminNoticePageRequest
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminNoticeController(
    private val adminNoticeService: AdminNoticeService
) : AdminNoticeApi {

    override fun getNotices(
        request: AdminNoticePageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<Unit>>> {
        adminNoticeService.getNotices(request)
        TODO("Not yet implemented")
    }
}

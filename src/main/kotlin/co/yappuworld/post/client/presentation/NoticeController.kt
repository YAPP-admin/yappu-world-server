package co.yappuworld.post.client.presentation

import co.yappuworld.global.response.CursorPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.post.client.application.NoticeService
import co.yappuworld.post.client.dto.request.NoticePageRequest
import co.yappuworld.post.client.dto.response.NoticeOverviewResponse
import co.yappuworld.post.client.dto.response.NoticeResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class NoticeController(
    private val noticeService: NoticeService
) : NoticeApi {

    override fun getNotices(
        request: NoticePageRequest
    ): ResponseEntity<SuccessResponse<CursorPageResponse<NoticeOverviewResponse, UUID>>> =
        ResponseEntity.ok(
            SuccessResponse(noticeService.getNotices(request))
        )

    override fun getNotice(noticeId: UUID): ResponseEntity<SuccessResponse<NoticeResponse>> =
        ResponseEntity.ok(
            SuccessResponse(noticeService.getNotice(noticeId))
        )
}

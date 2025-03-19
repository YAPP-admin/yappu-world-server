package co.yappuworld.post.client.presentation

import co.yappuworld.global.response.CursorPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.post.client.application.NoticeService
import co.yappuworld.post.client.dto.request.NoticePageRequest
import co.yappuworld.post.client.presentation.dto.response.NoticeApiResponseDto
import co.yappuworld.post.client.presentation.dto.response.NoticeOverviewApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class NoticeController(
    private val noticeService: NoticeService
) : NoticeApi {

    override fun getNotices(
        request: NoticePageRequest
    ): ResponseEntity<SuccessResponse<CursorPageResponse<NoticeOverviewApiResponseDto, UUID>>> {
        val response = noticeService.getNotices(request)
        val data = response.data.map { NoticeOverviewApiResponseDto(it) }
        return ResponseEntity.ok(
            SuccessResponse(
                CursorPageResponse(
                    data = data,
                    lastCursor = data.last().notice.id,
                    limit = request.limit,
                    hasNext = response.hasNext
                )
            )
        )
    }

    override fun getNotice(noticeId: UUID): ResponseEntity<SuccessResponse<NoticeApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                NoticeApiResponseDto(noticeService.getNotice(noticeId))
            )
        )
}

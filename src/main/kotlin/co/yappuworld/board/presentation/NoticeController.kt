package co.yappuworld.board.presentation

import co.yappuworld.board.application.BoardService
import co.yappuworld.board.presentation.dto.request.NoticePageApiRequestDto
import co.yappuworld.board.presentation.dto.response.NoticeOverviewApiResponseDto
import co.yappuworld.global.response.CursorPageResponse
import co.yappuworld.global.response.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class NoticeController(
    private val boardService: BoardService
) : NoticeApi {

    override fun getNotices(
        request: NoticePageApiRequestDto
    ): ResponseEntity<SuccessResponse<CursorPageResponse<NoticeOverviewApiResponseDto, UUID>>> {
        val response = boardService.getNotices(request.toAppRequest())
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
}

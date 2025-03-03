package co.yappuworld.board.presentation

import co.yappuworld.board.application.BoardService
import co.yappuworld.global.response.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class BoardController(
    private val boardService: BoardService
) : BoardApi {
    override fun getBoards(
        page: Int,
        size: Int,
        noticeType: String?
    ) = ResponseEntity.ok(
        SuccessResponse(
            boardService.readBoardPage(
                pageNumber = page,
                size = size,
                noticeType = noticeType
            )
        )
    )

    override fun getBoardDetail(boardId: String) =
        ResponseEntity.ok(
            SuccessResponse(
                boardService.readBoardDetail(
                    boardId = UUID.fromString(boardId)
                )
            )
        )
}

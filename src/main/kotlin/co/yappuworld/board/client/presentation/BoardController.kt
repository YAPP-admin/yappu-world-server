package co.yappuworld.board.client.presentation

import co.yappuworld.board.client.application.BoardService
import co.yappuworld.board.client.presentation.dto.response.BoardResponse
import co.yappuworld.global.response.SuccessResponse
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class BoardController(
    private val boardService: BoardService
) : BoardApi {

    //    override fun getBoards(
    //        page: Int,
    //        size: Int,
    //        noticeType: String?
    //    ) = ResponseEntity.ok(
    //        SuccessResponse(
    //            boardService.readBoardPage(
    //                pageNumber = page,
    //                size = size,
    //                noticeType = noticeType
    //            )
    //        )
    //    )

    override fun getBoards(
        lastCreatedAt: String?,
        limit: Int,
        noticeType: String?
    ): ResponseEntity<SuccessResponse<Page<BoardResponse>>> {
        TODO("Not yet implemented")
    }

    override fun getBoardDetail(boardId: String) =
        ResponseEntity.ok(
            SuccessResponse(
                boardService.readBoardDetail(
                    boardId = UUID.fromString(boardId)
                )
            )
        )
}

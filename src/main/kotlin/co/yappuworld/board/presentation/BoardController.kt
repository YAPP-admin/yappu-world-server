package co.yappuworld.board.presentation

import co.yappuworld.board.application.BoardService
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class BoardController(
    private val boardService: BoardService
) : BoardApi {
    override fun getBoards(
        securityUser: SecurityUser,
        page: Int,
        size: Int,
        noticeType: String?
    ) = ResponseEntity.ok(
        SuccessResponse(
            boardService.readBoardPage(
                userId = securityUser.userId,
                pageNumber = page,
                size = size,
                noticeType = noticeType
            )
        )
    )

    override fun getBoardDetail(
        securityUser: SecurityUser,
        boardId: String
    ) = ResponseEntity.ok(
        SuccessResponse(
            boardService.readBoardDetail(
                userId = securityUser.userId,
                boardId = UUID.fromString(boardId)
            )
        )
    )
}

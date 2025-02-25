package co.yappuworld.board.presentation

import co.yappuworld.board.application.BoardService
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class BoardController(
    private val boardService: BoardService
) : BoardApi {
    override fun getBoards(securityUser: SecurityUser, page: Int, size: Int) =
        ResponseEntity.ok(
            SuccessResponse.of(
                boardService.readBoardPage(
                    userId = securityUser.userId,
                    pageNumber = page,
                    size = size
                )
            )
        )
    
    override fun getBoardDetail(securityUser: SecurityUser, boardId: String) =
        ResponseEntity.ok(
            SuccessResponse.of(
                boardService.readBoardDetail(
                    userId = securityUser.userId,
                    boardId = UUID.fromString(boardId)
                )
            )
        )
}

package co.yappuworld.board.presentation

import org.springframework.web.bind.annotation.RestController

@RestController
class BoardController : BoardApi {
    override fun getBoards() {

    }

    override fun getBoardDetail(boardId: String) {
        TODO("Not yet implemented")
    }
}

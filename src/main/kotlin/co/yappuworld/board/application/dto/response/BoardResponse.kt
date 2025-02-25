package co.yappuworld.board.application.dto.response

import co.yappuworld.board.domain.model.Board
import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.User
import java.util.*

data class BoardResponse(
    val id: UUID,
    val boardType: String,
    val noticeType: String? = null,
    val title: String,
    val content: String,
    val displayTarget: String?,
    val writer: String
) {
    companion object {
        private const val POST_FIX = "기"

        fun of(board: Board, user: User, activityUnit: ActivityUnit): BoardResponse {
            return BoardResponse(
                id = board.id,
                boardType = board.boardType,
                noticeType = board.noticeType,
                title = board.title,
                content = board.content,
                displayTarget = board.displayTarget,
                writer = activityUnit.generation.toString() + POST_FIX + user.name
            )
        }
    }
}

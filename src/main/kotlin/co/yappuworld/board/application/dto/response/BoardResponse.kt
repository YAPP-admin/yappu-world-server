package co.yappuworld.board.application.dto.response

import co.yappuworld.board.domain.model.Board
import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.User
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BoardResponse(
    @Schema(description = "게시판 식별자")
    val id: UUID,
    @Schema(description = "게시판 타입 (NORMAL, NOTICE)")
    val boardType: String,
    @Schema(description = "공지 타입 (세션, 운영)")
    val noticeType: String? = null,
    @Schema(description = "제목")
    val title: String,
    @Schema(description = "내용")
    val content: String,
    val displayTarget: String?,
    @Schema(description = "작성자")
    val writer: String
) {
    companion object {
        private const val POST_FIX = "기"

        fun of(
            board: Board,
            user: User,
            activityUnit: ActivityUnit
        ): BoardResponse =
            BoardResponse(
                id = board.id,
                boardType = board.boardType,
                noticeType = board.noticeType,
                title = board.title,
                content = board.content,
                displayTarget = board.displayTarget,
                writer = activityUnit.generation.toString() + POST_FIX + " " + user.name
            )
    }
}

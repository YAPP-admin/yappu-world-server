package co.yappuworld.board.presentation.dto.response

import co.yappuworld.board.domain.model.Board
import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.User
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

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
    @Schema(description = "작성자")
    val writer: Writer,
    @Schema(description = "작성날짜")
    val createdAt: String
) {
    companion object {
        data class Writer(
            val name: String,
            val generation: Int
        )

        fun of(
            board: Board,
            user: User,
            activityUnit: ActivityUnit
        ): BoardResponse =
            BoardResponse(
                id = board.id,
                boardType = board.boardType.name,
                noticeType = board.noticeType?.label,
                title = board.title,
                content = board.content,
                createdAt = board.createdAt.toString(),
                writer = Writer(
                    name = user.name,
                    generation = activityUnit.generation
                )
            )
    }
}

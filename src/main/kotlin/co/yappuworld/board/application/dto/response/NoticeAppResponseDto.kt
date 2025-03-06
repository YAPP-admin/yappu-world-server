package co.yappuworld.board.application.dto.response

import co.yappuworld.board.domain.model.Notice
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

data class NoticeAppResponseDto(
    val notice: NoticeDetailsAppResponseDto,
    val writer: NoticeDetailsWriterAppResponseDto
) {
    constructor(notice: Notice, user: UserWithLastActivityUnit) : this(
        notice = NoticeDetailsAppResponseDto(notice),
        writer = NoticeDetailsWriterAppResponseDto(user)
    )
}

data class NoticeDetailsAppResponseDto(
    @Schema(description = "공지사항 ID")
    val id: UUID,
    @Schema(description = "공지사항 제목")
    val title: String,
    @Schema(description = "공지사항 내용")
    val content: String,
    @Schema(description = "공지사항 작성일")
    val createdAt: LocalDate
) {

    constructor(notice: Notice) : this(
        id = notice.id,
        title = notice.title,
        content = notice.content,
        createdAt = notice.createdAt.toLocalDate()
    )
}

data class NoticeDetailsWriterAppResponseDto(
    @Schema(description = "작성자 ID")
    val id: UUID,
    @Schema(description = "작성자 가장 최근 활동 기수")
    val activityUnitGeneration: Int,
    @Schema(description = "작성자 직군")
    val activityUnitPosition: Position
) {

    constructor(user: UserWithLastActivityUnit) : this(
        id = user.userId,
        activityUnitGeneration = user.activityUnit.generation,
        activityUnitPosition = user.activityUnit.position
    )
}

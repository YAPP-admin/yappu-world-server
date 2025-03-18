package co.yappuworld.board.client.application.dto.response

import co.yappuworld.board.domain.model.Notice
import co.yappuworld.board.domain.vo.NoticeType
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
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
    val id: UUID,
    val title: String,
    val content: String,
    val createdAt: LocalDate,
    val type: NoticeType
) {

    constructor(notice: Notice) : this(
        id = notice.id,
        title = notice.title,
        content = notice.content,
        createdAt = notice.createdAt.toLocalDate(),
        type = notice.noticeType
    )
}

data class NoticeDetailsWriterAppResponseDto(
    val id: UUID,
    val name: String,
    val activityUnitGeneration: Int,
    val activityUnitPosition: Position
) {

    constructor(user: UserWithLastActivityUnit) : this(
        id = user.userId,
        name = user.name,
        activityUnitGeneration = user.activityUnit.generation,
        activityUnitPosition = user.activityUnit.position
    )
}

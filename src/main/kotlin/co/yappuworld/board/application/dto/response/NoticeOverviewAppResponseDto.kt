package co.yappuworld.board.application.dto.response

import co.yappuworld.board.domain.model.Notice
import co.yappuworld.board.domain.vo.NoticeType
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import java.time.LocalDate
import java.util.UUID

data class NoticeOverviewAppResponseDto(
    val notice: NoticeSimpleAppResponseDto,
    val writer: NoticeOverviewWriterAppResponseDto
) {

    constructor(notice: Notice, user: UserWithLastActivityUnit) : this(
        notice = NoticeSimpleAppResponseDto(notice),
        writer = NoticeOverviewWriterAppResponseDto(user)
    )
}

data class NoticeSimpleAppResponseDto(
    val id: UUID,
    val createdAt: LocalDate,
    val title: String,
    val content: String,
    val noticeType: NoticeType
) {

    constructor(notice: Notice) : this(
        id = notice.id,
        createdAt = LocalDate.now(),
        title = notice.title,
        content = notice.contentSummary.take(200),
        noticeType = notice.noticeType
    )
}

data class NoticeOverviewWriterAppResponseDto(
    val userId: UUID,
    val name: String,
    val activityUnitGeneration: Int,
    val activityUnitPosition: Position
) {

    constructor(user: UserWithLastActivityUnit) : this(
        userId = user.userId,
        name = user.name,
        activityUnitGeneration = user.activityUnit.generation,
        activityUnitPosition = user.activityUnit.position
    )
}

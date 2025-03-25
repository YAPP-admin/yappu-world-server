package co.yappuworld.post.client.application.dto.response

import co.yappuworld.post.domain.NoticeEntity
import co.yappuworld.post.domain.NoticeType
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import java.time.LocalDate
import java.util.UUID

data class NoticeAppResponseDto(
    val notice: NoticeDetailsAppResponseDto,
    val writer: NoticeDetailsWriterAppResponseDto
) {

    companion object {
        fun from(
            notice: NoticeEntity,
            user: UserWithLastActivityUnit
        ): NoticeAppResponseDto =
            NoticeAppResponseDto(
                notice = NoticeDetailsAppResponseDto(notice),
                writer = NoticeDetailsWriterAppResponseDto(user)
            )
    }
}

data class NoticeDetailsAppResponseDto(
    val id: UUID,
    val title: String,
    val content: String,
    val createdAt: LocalDate,
    val type: NoticeType
) {

    constructor(notice: NoticeEntity) : this(
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

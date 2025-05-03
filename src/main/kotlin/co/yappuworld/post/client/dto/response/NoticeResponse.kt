package co.yappuworld.post.client.dto.response

import co.yappuworld.operation.client.dto.response.PositionResponse
import co.yappuworld.post.infrastructure.entity.NoticeEntity
import co.yappuworld.post.domain.NoticeType
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

data class NoticeResponse(
    @Schema(description = "공지사항 정보")
    val notice: NoticeDetailResponse,
    @Schema(description = "작성자 정보")
    val writer: NoticeDetailsWriterResponse
) {

    companion object {
        fun from(
            noticeEntity: NoticeEntity,
            user: UserWithLastActivityUnit
        ): NoticeResponse =
            NoticeResponse(
                notice = NoticeDetailResponse(noticeEntity),
                writer = NoticeDetailsWriterResponse(user)
            )
    }
}

data class NoticeDetailResponse(
    @Schema(description = "공지사항 ID")
    val id: UUID,
    @Schema(description = "공지사항 제목")
    val title: String,
    @Schema(description = "공지사항 내용")
    val content: String,
    @Schema(description = "공지사항 작성일")
    val createdAt: LocalDate,
    @Schema(description = "공지사항 종류")
    val noticeType: NoticeType
) {

    constructor(notice: NoticeEntity) : this(
        id = notice.id,
        title = notice.title,
        content = notice.content,
        createdAt = notice.createdAt.toLocalDate(),
        noticeType = notice.noticeType
    )
}

data class NoticeDetailsWriterResponse(
    @Schema(description = "작성자 ID")
    val id: UUID,
    @Schema(description = "작성자 이름")
    val name: String,
    @Schema(description = "작성자 가장 최근 활동 기수")
    val activityUnitGeneration: Int,
    @Schema(description = "작성자 직군")
    val activityUnitPosition: PositionResponse
) {

    constructor(writer: UserWithLastActivityUnit) : this(
        id = writer.userId,
        name = writer.name,
        activityUnitGeneration = writer.lastActiveGeneration,
        activityUnitPosition = PositionResponse(writer.lastActivePosition)
    )
}

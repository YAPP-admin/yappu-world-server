package co.yappuworld.post.client.dto.response

import co.yappuworld.operation.client.dto.response.PositionResponse
import co.yappuworld.post.infrastructure.entity.NoticeEntity
import co.yappuworld.post.domain.NoticeType
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

data class NoticeOverviewResponse(
    @Schema(description = "공지사항 정보")
    val notice: NoticeSimpleResponse,
    @Schema(description = "작성자 정보")
    val writer: NoticeOverviewWriterResponse
) {

    constructor(notice: NoticeEntity, writer: UserWithLastActivityUnit) : this(
        notice = NoticeSimpleResponse(notice),
        writer = NoticeOverviewWriterResponse(writer)
    )
}

data class NoticeSimpleResponse(
    @Schema(description = "공지사항 ID")
    val id: UUID,
    @Schema(description = "작성일")
    val createdAt: LocalDate,
    @Schema(description = "공지사항 제목")
    val title: String,
    @Schema(description = "공지사항 내용 (최대 200자)")
    val content: String,
    @Schema(description = "공지사항 종류")
    val noticeType: NoticeType
) {

    constructor(notice: NoticeEntity) : this(
        id = notice.id,
        createdAt = notice.createdAt.toLocalDate(),
        title = notice.title,
        content = notice.contentSummary.take(200),
        noticeType = notice.noticeType
    )
}

data class NoticeOverviewWriterResponse(
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

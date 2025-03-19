package co.yappuworld.post.client.presentation.dto.response

import co.yappuworld.post.client.application.dto.response.NoticeOverviewAppResponseDto
import co.yappuworld.post.client.application.dto.response.NoticeOverviewWriterAppResponseDto
import co.yappuworld.post.client.application.dto.response.NoticeSimpleAppResponseDto
import co.yappuworld.post.domain.vo.NoticeType
import co.yappuworld.operation.presentation.dto.response.PositionApiResponseDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

data class NoticeOverviewApiResponseDto(
    val notice: NoticeSimpleApiResponseDto,
    val writer: NoticeOverviewWriterApiResponseDto
) {

    constructor(response: NoticeOverviewAppResponseDto) : this(
        notice = NoticeSimpleApiResponseDto(response.notice),
        writer = NoticeOverviewWriterApiResponseDto(response.writer)
    )
}

data class NoticeSimpleApiResponseDto(
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

    constructor(notice: NoticeSimpleAppResponseDto) : this(
        id = notice.id,
        createdAt = notice.createdAt,
        title = notice.title,
        content = notice.content,
        noticeType = notice.noticeType
    )
}

data class NoticeOverviewWriterApiResponseDto(
    @Schema(description = "작성자 ID")
    val id: UUID,
    @Schema(description = "작성자 이름")
    val name: String,
    @Schema(description = "작성자 가장 최근 활동 기수")
    val activityUnitGeneration: Int,
    @Schema(description = "작성자 직군")
    val activityUnitPosition: PositionApiResponseDto
) {

    constructor(writer: NoticeOverviewWriterAppResponseDto) : this(
        id = writer.userId,
        name = writer.name,
        activityUnitGeneration = writer.activityUnitGeneration,
        activityUnitPosition = PositionApiResponseDto(writer.activityUnitPosition)
    )
}

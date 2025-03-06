package co.yappuworld.board.presentation.dto.response

import co.yappuworld.board.application.dto.response.NoticeAppResponseDto
import co.yappuworld.board.application.dto.response.NoticeDetailsAppResponseDto
import co.yappuworld.board.application.dto.response.NoticeDetailsWriterAppResponseDto
import co.yappuworld.operation.presentation.dto.response.PositionApiResponseDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

data class NoticeApiResponseDto(
    @Schema(description = "공지사항 정보")
    val notice: NoticeDetailsApiResponseDto,
    @Schema(description = "작성자 정보")
    val writer: NoticeDetailsWriterApiResponseDto
) {
    constructor(response: NoticeAppResponseDto) : this(
        notice = NoticeDetailsApiResponseDto(response.notice),
        writer = NoticeDetailsWriterApiResponseDto(response.writer)
    )
}

data class NoticeDetailsApiResponseDto(
    @Schema(description = "공지사항 ID")
    val id: UUID,
    @Schema(description = "공지사항 제목")
    val title: String,
    @Schema(description = "공지사항 내용")
    val content: String,
    @Schema(description = "공지사항 작성일")
    val createdAt: LocalDate
) {

    constructor(notice: NoticeDetailsAppResponseDto) : this(
        id = notice.id,
        title = notice.title,
        content = notice.content,
        createdAt = notice.createdAt
    )
}

data class NoticeDetailsWriterApiResponseDto(
    @Schema(description = "작성자 ID")
    val id: UUID,
    @Schema(description = "작성자 가장 최근 활동 기수")
    val activityUnitGeneration: Int,
    @Schema(description = "작성자 직군")
    val activityUnitPosition: PositionApiResponseDto
) {

    constructor(writer: NoticeDetailsWriterAppResponseDto) : this(
        id = writer.id,
        activityUnitGeneration = writer.activityUnitGeneration,
        activityUnitPosition = PositionApiResponseDto(writer.activityUnitPosition)
    )
}

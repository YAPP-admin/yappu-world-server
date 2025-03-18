package co.yappuworld.board.client.presentation.dto.request

import co.yappuworld.board.client.application.dto.request.NoticePageAppRequestDto
import co.yappuworld.board.domain.vo.NoticeType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class NoticePageApiRequestDto(
    @field:Schema(description = "지난 요청 마지막 데이터의 ID, 첫 요청인 경우 null", required = false)
    val lastCursorId: UUID?,
    @field:Schema(description = "요청 데이터 개수", required = true)
    @field:Min(value = 1L, message = "요청 데이터 개수는 최소 1개 이상이어야합니다.")
    val limit: Int,
    @field:Schema(description = "공지사항 필터", required = true)
    @field:NotNull(message = "공지사항 필터는 필수로 선택해야합니다.")
    val noticeType: NoticeTypeInPageRequest
) {

    fun toAppRequest(): NoticePageAppRequestDto =
        NoticePageAppRequestDto(
            lastBoardId = lastCursorId,
            limit = limit,
            noticeType = when (noticeType) {
                NoticeTypeInPageRequest.ALL -> null
                NoticeTypeInPageRequest.OPERATION -> NoticeType.OPERATION
                NoticeTypeInPageRequest.SESSION -> NoticeType.SESSION
            }
        )
}

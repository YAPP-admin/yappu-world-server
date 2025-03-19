package co.yappuworld.board.client.presentation

import co.yappuworld.board.client.dto.request.AdminNoticePageRequest
import co.yappuworld.board.client.dto.response.AdminNoticeDetailResponse
import co.yappuworld.board.client.dto.response.AdminNoticeSummaryResponse
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

@Tag(name = "어드민 공지사항 API", description = "공지사항 관리")
interface AdminNoticeApi {

    @Operation(summary = "공지사항 목록 조회")
    @GetMapping("/admin/v1/notices")
    fun getNotices(
        @Valid @ParameterObject request: AdminNoticePageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminNoticeSummaryResponse>>>

    @Operation(summary = "공지사항 상세 조회")
    @GetMapping("/admin/v1/notices/{noticeId}")
    fun getNotice(
        @PathVariable noticeId: UUID
    ): ResponseEntity<SuccessResponse<AdminNoticeDetailResponse>>
}

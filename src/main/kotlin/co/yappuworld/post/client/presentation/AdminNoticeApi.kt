package co.yappuworld.post.client.presentation

import co.yappuworld.post.client.dto.request.AdminNoticeCreateRequest
import co.yappuworld.post.client.dto.request.AdminNoticePageRequest
import co.yappuworld.post.client.dto.request.AdminNoticeUpdateRequest
import co.yappuworld.post.client.dto.response.AdminNoticeDetailResponse
import co.yappuworld.post.client.dto.response.AdminNoticeSummaryResponse
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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

    @Operation(summary = "공지사항 작성")
    @PostMapping("/admin/v1/notices")
    fun createNotice(
        @AuthenticationPrincipal securityUser: SecurityUser,
        @Valid @RequestBody request: AdminNoticeCreateRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "공지사항 수정")
    @PutMapping("/admin/v1/notices")
    fun updateNotice(
        @Valid @RequestBody request: AdminNoticeUpdateRequest
    ): ResponseEntity<Unit>
}

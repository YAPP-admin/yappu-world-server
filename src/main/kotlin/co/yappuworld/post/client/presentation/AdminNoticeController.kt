package co.yappuworld.post.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.post.client.application.AdminNoticeService
import co.yappuworld.post.client.dto.request.AdminNoticeCreateRequest
import co.yappuworld.post.client.dto.request.AdminNoticeDeleteRequest
import co.yappuworld.post.client.dto.request.AdminNoticePageRequest
import co.yappuworld.post.client.dto.request.AdminNoticeUpdateRequest
import co.yappuworld.post.client.dto.response.AdminNoticeDetailResponse
import co.yappuworld.post.client.dto.response.AdminNoticeSummaryResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
class AdminNoticeController(
    private val adminNoticeService: AdminNoticeService
) : AdminNoticeApi {

    override fun getNotices(
        request: AdminNoticePageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminNoticeSummaryResponse>>> =
        ResponseEntity.ok(
            SuccessResponse(adminNoticeService.getNotices(request))
        )

    override fun getNotice(noticeId: UUID): ResponseEntity<SuccessResponse<AdminNoticeDetailResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminNoticeService.getNotice(noticeId))
        )

    override fun createNotice(
        securityUser: SecurityUser,
        request: AdminNoticeCreateRequest
    ): ResponseEntity<Unit> =
        adminNoticeService.createNotice(securityUser.userId, request).let { noticeId ->
            ResponseEntity.created(URI.create("/admin/v1/notices/$noticeId")).build()
        }

    override fun updateNotice(request: AdminNoticeUpdateRequest): ResponseEntity<Unit> {
        adminNoticeService.updateNotice(request)
        return ResponseEntity.noContent().build()
    }

    override fun deleteNotice(request: AdminNoticeDeleteRequest): ResponseEntity<Unit> {
        adminNoticeService.deleteNotice(request)
        return ResponseEntity.noContent().build()
    }
}

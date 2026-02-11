package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.client.application.AdminUserService
import co.yappuworld.user.client.dto.request.AdminUserPageRequest
import co.yappuworld.user.client.dto.request.AdminUserUpdateRequest
import co.yappuworld.user.client.dto.response.AdminUserDetailResponse
import co.yappuworld.user.client.dto.response.AdminUserOverviewResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AdminUserManageController(
    private val adminUserService: AdminUserService
) : AdminUserManageApi {

    override fun getUserDetail(userId: UUID): ResponseEntity<SuccessResponse<AdminUserDetailResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminUserService.getUserDetail(userId))
        )

    override fun getUsers(
        request: AdminUserPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminUserOverviewResponse>>> =
        ResponseEntity.ok(
            SuccessResponse(adminUserService.getUserOverviews(request))
        )

    override fun updateUserDetails(request: AdminUserUpdateRequest): ResponseEntity<Unit> {
        request.checkRequest()
        adminUserService.updateUserDetails(request)
        return ResponseEntity.noContent().build()
    }

    override fun deactivateUser(userId: UUID): ResponseEntity<Unit> {
        adminUserService.deactivate(userId)
        return ResponseEntity.noContent().build()
    }
}

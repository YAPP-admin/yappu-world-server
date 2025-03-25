package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.client.dto.response.AdminUserDetailResponse
import co.yappuworld.user.client.application.UserAdminService
import co.yappuworld.user.client.dto.request.AdminUserPageRequest
import co.yappuworld.user.client.dto.request.AdminUserUpdateRequest
import co.yappuworld.user.client.dto.response.AdminUserOverviewResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserAdminController(
    private val userAdminService: UserAdminService
) : UserAdminApi {

    override fun getUserDetail(userId: UUID): ResponseEntity<SuccessResponse<AdminUserDetailResponse>> =
        ResponseEntity.ok(
            SuccessResponse(userAdminService.getUserDetail(userId))
        )

    override fun getUsers(
        request: AdminUserPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminUserOverviewResponse>>> =
        ResponseEntity.ok(
            SuccessResponse(userAdminService.getUserOverviews(request))
        )

    override fun updateUserDetails(request: AdminUserUpdateRequest) {
        userAdminService.updateUserDetails(request)
    }
}

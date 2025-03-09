package co.yappuworld.user.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.application.UserAdminService
import co.yappuworld.user.presentation.dto.request.AdminUserPageApiRequestDto
import co.yappuworld.user.presentation.dto.request.AdminUserUpdateApiRequestDto
import co.yappuworld.user.presentation.dto.response.AdminUserDetailsApiResponseDto
import co.yappuworld.user.presentation.dto.response.AdminUserOverviewApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserAdminController(
    private val userAdminService: UserAdminService
) : UserAdminApi {

    override fun getUserDetails(userId: UUID): ResponseEntity<SuccessResponse<AdminUserDetailsApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                AdminUserDetailsApiResponseDto(userAdminService.getUserDetails(userId))
            )
        )

    override fun getUsers(
        request: AdminUserPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminUserOverviewApiResponseDto>>> {
        val response = userAdminService.getUserOverviews(request.toAppRequest()).let { response ->
            OffsetPageResponse(
                data = response.data.map { AdminUserOverviewApiResponseDto(it) },
                totalCount = response.totalCount,
                totalPages = response.totalPages,
                page = request.page,
                size = request.size
            )
        }

        return ResponseEntity.ok(
            SuccessResponse(response)
        )
    }

    override fun updateUserDetails(request: AdminUserUpdateApiRequestDto) {
        userAdminService.updateUserDetails(request.toAppRequest())
    }
}

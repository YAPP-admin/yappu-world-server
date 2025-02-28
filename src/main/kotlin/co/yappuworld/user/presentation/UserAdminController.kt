package co.yappuworld.user.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.application.UserAdminService
import co.yappuworld.user.presentation.dto.response.AdminUserDetailsApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserAdminController(
    private val userAdminService: UserAdminService
) : UserAdminApi {

    override fun getUserDetails(userId: UUID): ResponseEntity<SuccessResponse<AdminUserDetailsApiResponseDto>> {
        return ResponseEntity.ok(
            SuccessResponse(
                AdminUserDetailsApiResponseDto(userAdminService.getUserDetails(userId))
            )
        )
    }
}

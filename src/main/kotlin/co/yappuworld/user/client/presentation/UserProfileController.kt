package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.client.application.UserProfileService
import co.yappuworld.user.client.dto.response.UserActivityHistoriesResponse
import co.yappuworld.user.client.dto.response.UserProfileResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserProfileController(
    private val userProfileService: UserProfileService
) : UserProfileApi {

    override fun getProfile(securityUser: SecurityUser): ResponseEntity<SuccessResponse<UserProfileResponse>> =
        ResponseEntity.ok(
            SuccessResponse(userProfileService.findUserProfile(securityUser.userId))
        )

    override fun getUserActivityHistories(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<UserActivityHistoriesResponse>> =
        ResponseEntity.ok(
            SuccessResponse(userProfileService.findUserActivityHistories(securityUser.userId))
        )
}

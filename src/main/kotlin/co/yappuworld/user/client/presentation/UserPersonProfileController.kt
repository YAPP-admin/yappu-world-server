package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.client.application.UserPersonProfileService
import co.yappuworld.user.client.dto.response.UserPersonProfileResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserPersonProfileController(
    private val userPersonProfileService: UserPersonProfileService
) : UserPersonProfileApi {

    override fun getUserPersonProfile(userId: UUID): ResponseEntity<SuccessResponse<UserPersonProfileResponse>> =
        ResponseEntity.ok(
            SuccessResponse(userPersonProfileService.getUserPersonProfile(userId))
        )
}

package co.yappuworld.user.client.presentation

import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.client.dto.request.UpdateFcmTokenRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "유저 시스템 설정 관련 API", description = "FCM, 기기 정보 수정")
interface UserSystemApi {

    @Operation(summary = "FCM 토큰 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "FCM 토큰 수정 성공",
                responseCode = "204",
                content = [Content()]
            )
        ]
    )
    @PutMapping("/v1/users/fcm")
    fun updateFcmToken(
        @AuthenticationPrincipal securityUser: SecurityUser,
        @Valid @RequestBody request: UpdateFcmTokenRequest
    ): ResponseEntity<Unit>
}

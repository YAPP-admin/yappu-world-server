package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.client.dto.request.UpdateDeviceAlarmRequest
import co.yappuworld.user.client.dto.response.MasterAlarmToggleResponse
import co.yappuworld.user.client.dto.response.UserAlarmStatusResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "유저 알람 API", description = "알람 설정")
interface UserAlarmApi {

    @Operation(summary = "유저 알람 설정 상태")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "유저 알림 설정 상태",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "isMasterEnabled": "true"
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/alarms")
    fun getAlarmStatus(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<UserAlarmStatusResponse>>

    @Operation(summary = "기기 알림 정보 업데이트")
    @ApiResponse(
        responseCode = "204",
        content = [Content()]
    )
    @PutMapping("/v1/alarms/device")
    fun updateDeviceAlarm(
        @AuthenticationPrincipal securityUser: SecurityUser,
        @RequestBody request: UpdateDeviceAlarmRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "마스터 알람 토글")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "유저 마스터 알림 On",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "isEnabled": "true"
                                        }
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "유저 마스터 알림 Off",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "isEnabled": "false"
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PatchMapping("/v1/alarms/master")
    fun toggleMasterAlarm(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<MasterAlarmToggleResponse>>
}

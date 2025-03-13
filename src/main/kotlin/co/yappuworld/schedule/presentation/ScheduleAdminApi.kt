package co.yappuworld.schedule.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.presentation.dto.request.AdminSessionDeleteApiRequestDto
import co.yappuworld.schedule.presentation.dto.request.AdminSessionPageApiRequestDto
import co.yappuworld.schedule.presentation.dto.request.AdminSessionUpdateApiRequestDto
import co.yappuworld.schedule.presentation.dto.request.SessionCreateApiRequestDto
import co.yappuworld.schedule.presentation.dto.response.AdminSessionDetailsApiResponseDto
import co.yappuworld.schedule.presentation.dto.response.AdminSessionOverviewApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "세션 어드민 API", description = "세션 생성, 수정, 삭제")
interface ScheduleAdminApi {

    @Operation(summary = "세션 생성")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                content = [Content()]
            )
        ]
    )
    @PostMapping("/admin/v1/sessions")
    fun createSchedule(
        @Valid @RequestBody request: SessionCreateApiRequestDto
    ): ResponseEntity<Unit>

    @Operation(summary = "세션 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "목록 제공",
                                value = """
                                    {
                                        "data": {
                                            "data": [
                                                {
                                                    "id": "f70dd406-ffe4-11ef-ad31-0242ac120002",
                                                    "generation": 25,
                                                    "type": "OFFLINE",
                                                    "title": "OT",
                                                    "place": "강북노동자복지관",
                                                    "date": "2024-11-01",
                                                    "time": "14:00:00",
                                                    "endTime": "18:00:00"
                                                },
                                                {
                                                    "id": "f70dd5ff-ffe4-11ef-ad31-0242ac120002",
                                                    "generation": 25,
                                                    "type": "TEAM",
                                                    "title": "팀세션",
                                                    "place": null,
                                                    "date": "2024-11-01",
                                                    "time": null,
                                                    "endTime": null
                                                }
                                            ],
                                            "totalCount": 3,
                                            "totalPages": 2,
                                            "page": 1,
                                            "size": 2
                                        },
                                        "isSuccess": true
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/admin/v1/sessions")
    fun getSessions(
        @Valid @ParameterObject request: AdminSessionPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSessionOverviewApiResponseDto>>>

    @Operation(summary = "세션 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                value = """
                                    {
                                        "data": {
                                            "id": "f70dd5ff-ffe4-11ef-ad31-0242ac120002",
                                            "name": "팀세션",
                                            "generation": 25,
                                            "place": null,
                                            "date": "2024-11-01",
                                            "time": null,
                                            "endTime": null,
                                            "sessionType": "TEAM"
                                        },
                                        "isSuccess": true
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "ID와 일치하는 세션이 존재하지 않습니다.",
                                value = """
                                    {
                                        "message": "세션을 찾지 못했습니다.",
                                        "errorCode": "SCH_1002",
                                        "isSuccess": false
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/admin/v1/sessions/{sessionId}")
    fun getSession(
        @PathVariable sessionId: UUID
    ): ResponseEntity<SuccessResponse<AdminSessionDetailsApiResponseDto>>

    @Operation(summary = "세션 삭제")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "ID와 일치하는 세션이 존재하지 않습니다.",
                                value = """
                                    {
                                        "message": "세션을 찾지 못했습니다.",
                                        "errorCode": "SCH_1002",
                                        "isSuccess": false
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @DeleteMapping("/admin/v1/sessions")
    fun deleteSession(
        @RequestBody request: AdminSessionDeleteApiRequestDto
    ): ResponseEntity<Unit>

    @Operation(summary = "세션 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "세션 타입이 아닌 일정에 대한 수정 요청",
                                value = """
                                    {
                                        "message": "세션 타입 수정만 요청 가능합니다.",
                                        "errorCode": "SCH_1003",
                                        "isSuccess": false
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "ID와 일치하는 세션이 존재하지 않습니다.",
                                value = """
                                    {
                                        "message": "세션을 찾지 못했습니다.",
                                        "errorCode": "SCH_1002",
                                        "isSuccess": false
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PutMapping("/admin/v1/sessions")
    fun updateSession(
        @RequestBody request: AdminSessionUpdateApiRequestDto
    ): ResponseEntity<Unit>
}

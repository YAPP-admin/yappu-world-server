package co.yappuworld.schedule.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class AdminSessionEligibleUsersParamRequest(
    @field:Schema(description = "세션 기수", required = true, nullable = false)
    val generation: Int
)

package co.yappuworld.schedule.client.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class AdminAttendanceCodeResponse(
    @field:Schema(description = "출석 코드", nullable = true)
    val code: String? = null
)

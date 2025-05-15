package co.yappuworld.schedule.client.dto.request

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class AdminAttendanceCodeUpdateRequest(
    @Schema(description = "출석 코드")
    @field:Min(value = 0L, message = "가입코드는 0보다 커야 합니다.")
    @field:Max(value = 9999L, message = "가입코드는 9999보다 작아야 합니다.")
    private val code: Int
) {

    @JsonIgnore
    fun getPaddedCode(): String = code.toString().padStart(4, '0')
}

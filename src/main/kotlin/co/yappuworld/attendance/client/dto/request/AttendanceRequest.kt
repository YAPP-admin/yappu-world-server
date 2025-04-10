package co.yappuworld.attendance.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length
import java.util.UUID

data class AttendanceRequest(
    @Schema(description = "세션 식별자")
    @field:NotNull(message = "세션 식별자는 필수입니다.")
    val sessionId: UUID,
    @Schema(description = "출석 코드")
    @field:Length(min = 4, max = 4, message = "출석 코드는 4자리여야 합니다.")
    val attendanceCode: String
)

package co.yappuworld.schedule.client.dto.request

import co.yappuworld.global.util.LocalDateRange
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class SessionQueryParamRequest(
    @field:Schema(description = "기수, null이라면 현재 활성화 된 기수를 처리", nullable = true)
    val generation: Int? = null,
    @field:Schema(
        description = "세션 시작일의 시작 범위(include), start와 end는 모두 null이거나, 모두 값이 있어야 합니다.",
        nullable = true,
        example = "2025-05-01",
        type = "string"
    )
    val start: LocalDate? = null,
    @field:Schema(
        description = "세션 시작일의 종료 범위(exclude), start와 end는 모두 null이거나, 모두 값이 있어야 합니다.",
        nullable = true,
        example = "2025-06-01",
        type = "string"
    )
    val end: LocalDate? = null
) {

    fun checkRequest() {
        if ((start == null && end != null) || (start != null && end == null)) {
            throw IllegalArgumentException("start와 end는 모두 null이거나 모두 null이 아니어야 합니다.")
        }
    }

    fun getDateRange(): LocalDateRange? {
        if (start == null || end == null) {
            return null
        }

        return LocalDateRange(start, end.minusDays(1))
    }
}

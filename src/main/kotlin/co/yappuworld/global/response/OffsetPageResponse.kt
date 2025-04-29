package co.yappuworld.global.response

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

class OffsetPageResponse<T>(
    @Schema(description = "데이터 목록")
    val data: List<T>,
    @Schema(description = "전체 개수")
    val totalCount: Long,
    @Schema(description = "전체 페이지 수")
    val totalPages: Int,
    @Schema(description = "현재 페이지 번호")
    val page: Int,
    @Schema(description = "페이지 당 조회 개수")
    val size: Int
) {

    constructor(content: List<T>, data: Page<*>) : this(
        data = content,
        totalCount = data.totalElements,
        totalPages = data.totalPages,
        page = data.number + 1,
        size = data.size
    )

    companion object {
        fun <E, T> from(
            data: Page<E>,
            convert: (E) -> T
        ): OffsetPageResponse<T> =
            OffsetPageResponse(
                data = data.content.map(convert),
                totalCount = data.totalElements,
                totalPages = data.totalPages,
                page = data.number + 1,
                size = data.size
            )
    }
}

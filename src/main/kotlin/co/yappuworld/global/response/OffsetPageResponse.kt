package co.yappuworld.global.response

import io.swagger.v3.oas.annotations.media.Schema

class OffsetPageResponse<T : Any>(
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
)

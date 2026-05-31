package co.yappuworld.global.response

import io.swagger.v3.oas.annotations.media.Schema

class CursorPageResponse<T, R>(
    @field:Schema(description = "데이터 목록")
    val data: List<T>,
    @field:Schema(description = "다음 요청에 사용되는 커서 정보, 사용 방법은 API 별로 상이할 수 있음")
    val lastCursor: R?,
    @field:Schema(description = "조회 개수")
    val limit: Int,
    @field:Schema(description = "다음 데이터 존재 여부")
    val hasNext: Boolean
)

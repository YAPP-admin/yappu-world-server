package co.yappuworld.user.application.dto.response

data class UserOverviewBundleAppResponseDto(
    val data: List<UserOverviewAppResponseDto>,
    val totalCount: Long,
    val totalPages: Int
)

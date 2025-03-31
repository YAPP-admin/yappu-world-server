package co.yappuworld.post.client.application.dto.response

import co.yappuworld.post.domain.NoticeEntity
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import kotlin.math.min

data class NoticeBundleAppResponseDto(
    val data: List<NoticeOverviewAppResponseDto>,
    val hasNext: Boolean
) {
    companion object {

        fun from(
            notices: List<NoticeEntity>,
            users: List<UserWithLastActivityUnit>,
            limit: Int
        ): NoticeBundleAppResponseDto =
            users.associateBy { it.userId }.let { userById ->
                NoticeBundleAppResponseDto(
                    data = notices
                        .subList(0, min(limit, notices.size))
                        .map { n ->
                            NoticeOverviewAppResponseDto(n, userById[n.writerId]!!)
                        },
                    hasNext = notices.size > limit
                )
            }
    }
}

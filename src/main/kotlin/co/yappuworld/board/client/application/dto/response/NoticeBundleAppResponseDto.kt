package co.yappuworld.board.client.application.dto.response

import co.yappuworld.board.domain.model.Notice
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import kotlin.math.min

data class NoticeBundleAppResponseDto(
    val data: List<co.yappuworld.board.client.application.dto.response.NoticeOverviewAppResponseDto>,
    val hasNext: Boolean
) {
    companion object {

        fun from(
            notices: List<Notice>,
            users: List<UserWithLastActivityUnit>,
            limit: Int
        ): co.yappuworld.board.client.application.dto.response.NoticeBundleAppResponseDto =
            users.associateBy { it.userId }.let { userById ->
                co.yappuworld.board.client.application.dto.response.NoticeBundleAppResponseDto(
                    data = notices
                        .subList(0, min(limit, notices.size))
                        .map { n ->
                            co.yappuworld.board.client.application.dto.response.NoticeOverviewAppResponseDto(
                                n,
                                userById[n.writer.writerId]!!
                            )
                        },
                    hasNext = notices.size > limit
                )
            }
    }
}

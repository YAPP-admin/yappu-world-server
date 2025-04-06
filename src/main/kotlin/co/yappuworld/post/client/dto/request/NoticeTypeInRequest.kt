package co.yappuworld.post.client.dto.request

import co.yappuworld.post.client.dto.request.NoticeTypeInRequest.ALL
import co.yappuworld.post.client.dto.request.NoticeTypeInRequest.OPERATION
import co.yappuworld.post.client.dto.request.NoticeTypeInRequest.SESSION
import co.yappuworld.post.domain.NoticeType

/**
 * @property ALL 전체
 * @property OPERATION 운영
 * @property SESSION 세션
 */
enum class NoticeTypeInRequest {
    ALL,
    OPERATION,
    SESSION;

    fun toDomainType(): NoticeType? =
        when (this) {
            ALL -> null
            OPERATION -> NoticeType.OPERATION
            SESSION -> NoticeType.SESSION
        }
}

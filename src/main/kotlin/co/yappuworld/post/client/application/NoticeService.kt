package co.yappuworld.post.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.CursorPageResponse
import co.yappuworld.post.client.dto.request.NoticePageRequest
import co.yappuworld.post.client.dto.response.NoticeOverviewResponse
import co.yappuworld.post.client.dto.response.NoticeResponse
import co.yappuworld.post.domain.PostError
import co.yappuworld.post.infrastructure.PostFindService
import co.yappuworld.post.infrastructure.entity.NoticeEntity
import co.yappuworld.user.infrastructure.UserFindService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.min

private val logger = KotlinLogging.logger { }

@Service
class NoticeService(
    private val postFindService: PostFindService,
    private val userFindService: UserFindService
) {

    @Transactional(readOnly = true)
    fun getNotices(request: NoticePageRequest): CursorPageResponse<NoticeOverviewResponse, UUID> {
        val notices = postFindService.findAllNotices(
            limit = request.limit + 1,
            noticeType = request.noticeType.toDomainType(),
            lastNoticeId = request.lastCursorId
        )
        val users = userFindService
            .findAllUserLastActivityUnit(
                notices.map { it.writerId }.subList(0, min(request.limit, notices.size)).toSet()
            ).associateBy { it.userId }

        val data = notices
            .map { notice ->
                val user = users[notice.writerId]
                if (user == null) {
                    logger.error { "게시물 작성자의 ID ${notice.writerId}를 유저 테이블에서 찾을 수 없었습니다." }
                    throw BusinessException(PostError.NOTICE_WRITER_NOT_FOUND)
                }
                NoticeOverviewResponse(notice, user)
            }.subList(0, min(request.limit, notices.size))

        return CursorPageResponse(
            data = data,
            lastCursor = data.lastOrNull()?.notice?.id,
            limit = request.limit,
            hasNext = notices.size > request.limit
        )
    }

    @Transactional(readOnly = true)
    fun getNotice(noticeId: UUID): NoticeResponse {
        val notice = postFindService.findByIdOrNull(noticeId)
            ?: throw BusinessException(PostError.POST_NOT_FOUND)
        val user = userFindService.findUserLastActivityUnit(notice.writerId)

        return NoticeResponse.from(notice as NoticeEntity, user)
    }
}

package co.yappuworld.post.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.CursorPageResponse
import co.yappuworld.post.client.dto.request.NoticePageRequest
import co.yappuworld.post.client.dto.request.NoticeTypeInRequest.ALL
import co.yappuworld.post.client.dto.response.NoticeOverviewResponse
import co.yappuworld.post.client.dto.response.NoticeResponse
import co.yappuworld.post.domain.PostError
import co.yappuworld.post.domain.NoticeEntity
import co.yappuworld.post.domain.NoticeType
import co.yappuworld.post.infrastructure.PostRepository
import co.yappuworld.user.infrastructure.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.min

private val logger = KotlinLogging.logger { }

@Service
class NoticeService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getNotices(request: NoticePageRequest): CursorPageResponse<NoticeOverviewResponse, UUID> {
        val notices = this.getNoticeModels(request)
        val users = userRepository
            .findUsersWithActivityUnit(
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
            lastCursor = data.last().notice.id,
            limit = request.limit,
            hasNext = notices.size > request.limit
        )
    }

    @Transactional(readOnly = true)
    fun getNotice(noticeId: UUID): NoticeResponse {
        val notice = postRepository.findByIdOrNull(noticeId)
            ?: throw BusinessException(PostError.POST_NOT_FOUND)
        val user = userRepository.findUserWithActivityUnit(notice.writerId)

        return NoticeResponse.from(notice as NoticeEntity, user)
    }

    private fun getNoticeModels(request: NoticePageRequest): List<NoticeEntity> =
        when {
            request.lastCursorId != null && request.noticeType != ALL -> postRepository.findAllNotices(
                limit = request.limit + 1,
                noticeType = NoticeType.valueOf(request.noticeType.name),
                lastPostId = request.lastCursorId
            )
            request.lastCursorId != null && request.noticeType == ALL -> postRepository.findAllNotices(
                limit = request.limit + 1,
                lastPostId = request.lastCursorId
            )
            request.lastCursorId == null && request.noticeType != ALL -> postRepository.findAllNotices(
                limit = request.limit + 1,
                noticeType = NoticeType.valueOf(request.noticeType.name)
            )
            else -> postRepository.findAllNotices(
                limit = request.limit + 1
            )
        }
}

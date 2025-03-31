package co.yappuworld.post.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.post.client.application.dto.response.NoticeAppResponseDto
import co.yappuworld.post.client.application.dto.response.NoticeBundleAppResponseDto
import co.yappuworld.post.client.dto.request.NoticePageRequest
import co.yappuworld.post.client.dto.request.NoticeTypeInRequest.ALL
import co.yappuworld.post.domain.BoardError
import co.yappuworld.post.domain.NoticeEntity
import co.yappuworld.post.domain.NoticeType
import co.yappuworld.post.infrastructure.PostRepository
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.min

@Service
class NoticeService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getNotices(request: NoticePageRequest): NoticeBundleAppResponseDto {
        val notices = this.getNoticeModels(request)
        val users = userRepository.findUsersWithActivityUnit(
            notices.map { it.writerId }.subList(0, min(request.limit, notices.size)).toSet()
        )

        return NoticeBundleAppResponseDto.from(
            notices,
            users,
            request.limit
        )
    }

    @Transactional(readOnly = true)
    fun getNotice(noticeId: UUID): NoticeAppResponseDto {
        val notice = postRepository.findByIdOrNull(noticeId)
            ?: throw BusinessException(BoardError.BOARD_NOT_FOUND)
        val user = userRepository.findUserWithActivityUnit(notice.writerId)

        return NoticeAppResponseDto.from(notice as NoticeEntity, user)
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

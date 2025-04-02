package co.yappuworld.post.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.post.client.dto.request.AdminNoticeCreateRequest
import co.yappuworld.post.client.dto.request.AdminNoticeDeleteRequest
import co.yappuworld.post.client.dto.request.AdminNoticePageRequest
import co.yappuworld.post.client.dto.request.AdminNoticeUpdateRequest
import co.yappuworld.post.client.dto.response.AdminNoticeDetailResponse
import co.yappuworld.post.client.dto.response.AdminNoticeDetailWriterResponse
import co.yappuworld.post.client.dto.response.AdminNoticeSummaryResponse
import co.yappuworld.post.domain.NoticeEntity
import co.yappuworld.post.domain.PostError
import co.yappuworld.post.infrastructure.PostCommandService
import co.yappuworld.post.infrastructure.PostFindService
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminNoticeService(
    private val postFindService: PostFindService,
    private val postCommandService: PostCommandService,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getNotices(request: AdminNoticePageRequest): OffsetPageResponse<AdminNoticeSummaryResponse> {
        val page = postFindService.findAllByNoticeType(
            noticeType = request.noticeTypeOrNull,
            request.toPageRequest()
        )

        val userById = userRepository
            .findAllByIdIn(page.content.map { notice -> notice.writerId })
            .associateBy { it.id }

        return OffsetPageResponse(
            data = page.content.map { notice ->
                userById
                    .getOrElse(notice.writerId) { throw BusinessException(PostError.NOTICE_WRITER_NOT_FOUND) }
                    .let { user -> AdminNoticeSummaryResponse(notice as NoticeEntity, user) }
            },
            page = request.page,
            size = request.size,
            totalCount = page.totalElements,
            totalPages = page.totalPages
        )
    }

    @Transactional(readOnly = true)
    fun getNotice(noticeId: UUID): AdminNoticeDetailResponse {
        val notice = postFindService.findByIdOrNull(noticeId)?.let { it as NoticeEntity }
            ?: throw BusinessException(PostError.NOTICE_NOT_FOUND)

        val writer = userRepository.findByIdOrNull(notice.writerId)
            ?: throw BusinessException(PostError.NOTICE_WRITER_NOT_FOUND)

        return AdminNoticeDetailResponse(
            noticeId = notice.id,
            createdAt = notice.createdAt.toString(),
            title = notice.title,
            content = notice.content,
            type = notice.noticeType,
            writer = AdminNoticeDetailWriterResponse(writer)
        )
    }

    @Transactional
    fun createNotice(
        writerId: UUID,
        request: AdminNoticeCreateRequest
    ): UUID {
        val notice = NoticeEntity(
            title = request.title,
            content = request.content,
            contentSummary = request.plainContent.take(200),
            writerId = writerId,
            noticeType = request.type
        ).run { postCommandService.save(this) }

        return notice.id
    }

    @Transactional
    fun updateNotice(request: AdminNoticeUpdateRequest) {
        val notice = postFindService.findByIdOrNull(request.id)?.let { it as NoticeEntity }
            ?: throw BusinessException(PostError.NOTICE_NOT_FOUND)

        notice.update(
            title = request.title,
            content = request.content,
            contentSummary = request.plainContent.take(200),
            noticeType = request.type
        )
    }

    @Transactional
    fun deleteNotice(request: AdminNoticeDeleteRequest) {
        val notices = postFindService.findAllByIdIn(request.noticeIds)
        if (notices.size != request.size) {
            throw BusinessException(PostError.NOT_CONTAIN_DELETE_NOTICE)
        }

        postCommandService.deleteAll(notices)
    }
}

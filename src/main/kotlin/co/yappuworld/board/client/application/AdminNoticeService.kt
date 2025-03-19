package co.yappuworld.board.client.application

import co.yappuworld.board.client.dto.request.AdminNoticePageRequest
import co.yappuworld.board.client.dto.response.AdminNoticeDetailResponse
import co.yappuworld.board.client.dto.response.AdminNoticeDetailWriterResponse
import co.yappuworld.board.client.dto.response.AdminNoticeSummaryResponse
import co.yappuworld.board.domain.model.NoticeEntity
import co.yappuworld.board.domain.vo.BoardError
import co.yappuworld.board.domain.vo.NoticeType
import co.yappuworld.board.infrastructure.PostJpaRepository
import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AdminNoticeService(
    private val postRepository: PostJpaRepository,
    private val userRepository: UserRepository
) {

    fun getNotices(request: AdminNoticePageRequest): OffsetPageResponse<AdminNoticeSummaryResponse> {
        val page = when (request.noticeType == "ALL") {
            true -> postRepository.findAll(request.toPageRequest())
            false -> postRepository.findAllByNoticeType(NoticeType.valueOf(request.noticeType), request.toPageRequest())
        }

        val userById = userRepository
            .findAllByIdIn(page.content.map { notice -> notice.writerId })
            .associateBy { it.id }

        return OffsetPageResponse(
            data = page.content.map { notice ->
                userById
                    .getOrElse(notice.writerId) { throw BusinessException(BoardError.NOTICE_WRITER_NOT_FOUND) }
                    .let { user -> AdminNoticeSummaryResponse(notice as NoticeEntity, user) }
            },
            page = request.page,
            size = request.size,
            totalCount = page.totalElements,
            totalPages = page.totalPages
        )
    }

    fun getNotice(noticeId: UUID): AdminNoticeDetailResponse {
        val notice = postRepository
            .findById(noticeId)
            .orElseThrow { BusinessException(BoardError.NOTICE_NOT_FOUND) }
            as NoticeEntity
        val writer = userRepository
            .findById(notice.writerId)
            .orElseThrow { BusinessException(BoardError.NOTICE_WRITER_NOT_FOUND) }

        return AdminNoticeDetailResponse(
            noticeId = notice.id,
            createdAt = notice.createdAt.toString(),
            title = notice.title,
            content = notice.content,
            type = notice.noticeType,
            writer = AdminNoticeDetailWriterResponse(writer)
        )
    }
}

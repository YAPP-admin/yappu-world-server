package co.yappuworld.board.client.application

import co.yappuworld.board.client.dto.request.AdminNoticePageRequest
import co.yappuworld.board.client.dto.response.AdminNoticeSummaryResponse
import co.yappuworld.board.domain.vo.BoardError
import co.yappuworld.board.infrastructure.PostJpaRepository
import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.stereotype.Service

@Service
class AdminNoticeService(
    private val postRepository: PostJpaRepository,
    private val userRepository: UserRepository
) {

    fun getNotices(request: AdminNoticePageRequest): OffsetPageResponse<AdminNoticeSummaryResponse> {
        val page = postRepository.findAllByNoticeType(request.toNoticeType(), request.toPageRequest())
        val userById = userRepository
            .findAllByIdIn(page.content.map { notice -> notice.writerId })
            .associateBy { it.id }

        return OffsetPageResponse(
            data = page.content.map { notice ->
                userById
                    .getOrElse(notice.writerId) { throw BusinessException(BoardError.NOTICE_WRITER_NOT_FOUND) }
                    .let { user -> AdminNoticeSummaryResponse(notice, user) }
            },
            page = request.page,
            size = request.size,
            totalCount = page.totalElements,
            totalPages = page.totalPages
        )
    }
}

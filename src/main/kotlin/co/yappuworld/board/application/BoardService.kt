package co.yappuworld.board.application

import co.yappuworld.board.application.dto.request.NoticePageAppRequestDto
import co.yappuworld.board.application.dto.response.NoticeAppResponseDto
import co.yappuworld.board.application.dto.response.NoticeBundleAppResponseDto
import co.yappuworld.board.domain.model.Board
import co.yappuworld.board.domain.model.Notice
import co.yappuworld.board.domain.vo.BoardError
import co.yappuworld.board.infrastructure.BoardRepository
import co.yappuworld.board.infrastructure.NoticeRepository
import co.yappuworld.board.presentation.dto.response.BoardResponse
import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.min

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val noticeRepository: NoticeRepository,
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository
) {
    @Transactional(readOnly = true)
    fun readBoardPage(
        pageNumber: Int,
        size: Int,
        noticeType: String?
    ): Page<BoardResponse> =
        getBoards(
            noticeType = noticeType,
            pageable = PageRequest.of(pageNumber, size)
        ).map { board ->
            buildBoardResponse(board)
        }

    @Transactional(readOnly = true)
    fun getNotices(request: NoticePageAppRequestDto): NoticeBundleAppResponseDto {
        val notices = this.getNoticeModels(request)
        val users = userRepository.findUsersWithActivityUnit(
            notices.map { it.writer.writerId.toString() }.subList(0, min(request.limit, notices.size)).toSet()
        )

        return NoticeBundleAppResponseDto.from(notices, users, request.limit)
    }

    @Transactional(readOnly = true)
    fun getNotice(noticeId: UUID): NoticeAppResponseDto {
        val notice = noticeRepository.findByIdOrNull(noticeId)
            ?: throw BusinessException(BoardError.BOARD_NOT_FOUND)
        val user = userRepository.findUserWithActivityUnit(notice.writer.writerId.toString())

        return NoticeAppResponseDto(notice, user)
    }

    @Transactional(readOnly = true)
    fun readBoardDetail(boardId: UUID): BoardResponse {
        val board = boardRepository.findBoardByIdAndIsActiveTrue(boardId)
            ?: throw BusinessException(BoardError.BOARD_NOT_FOUND)

        return buildBoardResponse(board)
    }

    private fun buildBoardResponse(board: Board) =
        BoardResponse.of(
            board = board,
            user = userRepository.findByIdOrNull(board.writer.writerId)
                ?: throw BusinessException(UserError.USER_NOT_FOUND),
            activityUnit = activityUnitRepository
                .findAllByUserIdOrderByGenerationDesc(board.writer.writerId)
                .firstOrNull() ?: throw BusinessException(UserError.USER_RELATED_DATA_NOT_FOUND)
        )

    private fun getBoards(
        noticeType: String?,
        pageable: Pageable
    ): Page<Board> =
        if (noticeType != null) {
            boardRepository.findAllByIsActiveTrueAndNoticeTypeOrderByCreatedAtDesc(
                noticeType = noticeType,
                pageable = pageable
            )
        } else {
            boardRepository.findAllByIsActiveTrueOrderByCreatedAtDesc(pageable = pageable)
        }

    private fun getNoticeModels(request: NoticePageAppRequestDto): List<Notice> =
        when {
            request.lastBoardId != null && request.noticeType != null -> noticeRepository.findNotices(
                limit = request.limit + 1,
                noticeType = request.noticeType,
                lastBoardId = request.lastBoardId
            )
            request.lastBoardId != null && request.noticeType == null -> noticeRepository.findNotices(
                limit = request.limit + 1,
                lastBoardId = request.lastBoardId
            )
            request.lastBoardId == null && request.noticeType != null -> noticeRepository.findNotices(
                limit = request.limit + 1,
                noticeType = request.noticeType
            )
            else -> noticeRepository.findNotices(
                limit = request.limit + 1
            )
        }
}

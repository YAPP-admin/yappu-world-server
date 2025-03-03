package co.yappuworld.board.application

import co.yappuworld.board.application.dto.response.BoardResponse
import co.yappuworld.board.domain.model.Board
import co.yappuworld.board.domain.vo.BoardError
import co.yappuworld.board.infrastructure.BoardRepository
import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository
) {
    @Transactional(readOnly = true)
    fun readBoardPage(
        userId: UUID,
        pageNumber: Int,
        size: Int,
        noticeType: String?
    ): Page<BoardResponse> {
        val user = userRepository.findByIdOrNull(userId) ?: throw BusinessException(UserError.USER_NOT_FOUND)

        return if (noticeType != null) {
            boardRepository.findAllByIsActiveTrueAndNoticeTypeOrderByCreatedAtDesc(
                noticeType = noticeType,
                pageable = PageRequest.of(pageNumber, size)
            )
        } else {
            boardRepository.findAllByIsActiveTrueOrderByCreatedAtDesc(pageable = PageRequest.of(pageNumber, size))
        }.map { board ->
            board.filterInfoByRole(userRole = user.role)
        }.map { board ->
            buildBoardResponse(board)
        }
    }

    @Transactional(readOnly = true)
    fun readBoardDetail(
        userId: UUID,
        boardId: UUID
    ): BoardResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw BusinessException(UserError.USER_NOT_FOUND)
        val board = boardRepository.findBoardByIdAndIsActiveTrue(boardId)?.filterInfoByRole(user.role)
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
}

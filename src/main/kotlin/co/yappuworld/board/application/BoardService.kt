package co.yappuworld.board.application

import co.yappuworld.board.application.dto.response.BoardResponse
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
import java.util.*

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository
) {
    fun readBoardPage(userId: UUID, pageNumber: Int, size: Int): Page<BoardResponse> {
        val user = userRepository.findByIdOrNull(userId) ?: throw BusinessException(UserError.USER_NOT_FOUND)

        return boardRepository.findAllByIsActiveOrderByCreatedAtDesc(
            isActive = true,
            pageable = PageRequest.of(pageNumber, size)
        ).map { board ->
            board.filterInfoByRole(userRole = user.role)
        }.map { board ->
            BoardResponse.of(
                board = board,
                user = userRepository.findByIdOrNull(board.writer.writerId)
                    ?: throw BusinessException(UserError.USER_NOT_FOUND),
                activityUnit = activityUnitRepository.findAllByUserIdOrderByGenerationDesc(board.writer.writerId)
                    .firstOrNull() ?: throw BusinessException(UserError.USER_RELATED_DATA_NOT_FOUND)
            )
        }
    }

    fun readBoardDetail(userId: UUID, boardId: UUID): BoardResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw BusinessException(UserError.USER_NOT_FOUND)
        val board = boardRepository.findByIdOrNull(boardId)?.filterInfoByRole(user.role)
            ?: throw BusinessException(BoardError.BOARD_NOT_FOUND)

        return BoardResponse.of(
            board = board,
            user = userRepository.findByIdOrNull(board.writer.writerId)
                ?: throw BusinessException(UserError.USER_NOT_FOUND),
            activityUnit = activityUnitRepository.findAllByUserIdOrderByGenerationDesc(board.writer.writerId)
                .firstOrNull() ?: throw BusinessException(UserError.USER_RELATED_DATA_NOT_FOUND)
        )
    }
}

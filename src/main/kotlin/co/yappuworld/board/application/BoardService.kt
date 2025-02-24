package co.yappuworld.board.application

import co.yappuworld.board.domain.Board
import co.yappuworld.board.infrastructure.BoardRepository
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {
    fun readBoardPage(writerId: UUID, pageNumber: Int, size: Int): Page<Board> {
        val user = userRepository.findById(writerId).orElseThrow()

        return boardRepository.findAllByIsActiveOrderByCreatedAtDesc(
            isActive = true,
            pageable = PageRequest.of(pageNumber, size)
        ).map { board ->
            board.filterInfo(userRole = user.role)
        }
    }
}

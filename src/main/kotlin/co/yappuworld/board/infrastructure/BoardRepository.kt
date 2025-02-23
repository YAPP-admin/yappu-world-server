package co.yappuworld.board.infrastructure

import co.yappuworld.board.domain.Board
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BoardRepository : CrudRepository<Board, UUID>, PagingAndSortingRepository<Board, UUID> {
    fun findBoardByIdAndIsActive(id: UUID, isActive: Boolean): Board?

    fun findAllByIsActiveOrderByCreatedAtDesc(isActive: Boolean, pageable: Pageable): Page<Board>

}

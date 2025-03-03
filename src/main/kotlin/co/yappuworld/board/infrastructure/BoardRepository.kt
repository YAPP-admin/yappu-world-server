package co.yappuworld.board.infrastructure

import co.yappuworld.board.domain.model.Board
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BoardRepository :
    CrudRepository<Board, UUID>,
    PagingAndSortingRepository<Board, UUID> {
    fun findBoardByIdAndIsActiveTrue(id: UUID): Board?

    fun findAllByIsActiveTrueOrderByCreatedAtDesc(pageable: Pageable): Page<Board>

    fun findAllByIsActiveTrueAndNoticeTypeOrderByCreatedAtDesc(
        noticeType: String,
        pageable: Pageable
    ): Page<Board>
}

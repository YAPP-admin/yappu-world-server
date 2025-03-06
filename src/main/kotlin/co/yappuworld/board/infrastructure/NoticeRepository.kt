package co.yappuworld.board.infrastructure

import co.yappuworld.board.domain.model.Notice
import co.yappuworld.board.domain.vo.NoticeType
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface NoticeRepository : CrudRepository<Notice, UUID> {

    @Query(
        """
            SELECT *
            FROM boards b
            WHERE b.notice_type = :noticeType
                AND b.id < :lastBoardId
            ORDER BY b.id DESC
            LIMIT :limit;
        """
    )
    fun findNotices(
        limit: Int,
        noticeType: NoticeType,
        lastBoardId: UUID
    ): List<Notice>

    @Query(
        """
            SELECT *
            FROM boards b
            WHERE b.notice_type = :noticeType
            ORDER BY b.id DESC
            LIMIT :limit;
        """
    )
    fun findNotices(
        limit: Int,
        noticeType: NoticeType
    ): List<Notice>

    @Query(
        """
            SELECT *
            FROM boards b
            WHERE b.id < :lastBoardId
            ORDER BY b.id DESC
            LIMIT :limit;
        """
    )
    fun findNotices(
        limit: Int,
        lastBoardId: UUID
    ): List<Notice>

    @Query(
        """
            SELECT *
            FROM boards b
            ORDER BY b.id DESC
            LIMIT :limit;
        """
    )
    fun findNotices(limit: Int): List<Notice>
}

package co.yappuworld.post.infrastructure

import co.yappuworld.post.domain.model.NoticeEntity
import co.yappuworld.post.domain.model.PostEntity
import co.yappuworld.post.domain.vo.NoticeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PostJpaRepository : JpaRepository<PostEntity, UUID> {

    @Query(
        value = """
            SELECT n
            FROM NoticeEntity n
            WHERE n.noticeType = :noticeType
                AND n.id < :lastPostId
            ORDER BY n.id DESC
            LIMIT :limit
        """
    )
    fun findAllNotices(
        limit: Int,
        noticeType: NoticeType,
        lastPostId: UUID
    ): List<NoticeEntity>

    @Query(
        value = """
            SELECT n
            FROM NoticeEntity n
            WHERE n.noticeType = :noticeType
            ORDER BY n.id DESC
            LIMIT :limit
        """
    )
    fun findAllNotices(
        limit: Int,
        noticeType: NoticeType
    ): List<NoticeEntity>

    @Query(
        value = """
            SELECT n
            FROM NoticeEntity n
            WHERE n.id < :lastPostId
            ORDER BY n.id DESC
            LIMIT :limit
        """
    )
    fun findAllNotices(
        limit: Int,
        lastPostId: UUID
    ): List<NoticeEntity>

    @Query(
        value = """
            SELECT n
            FROM NoticeEntity n
            ORDER BY n.id DESC
            LIMIT :limit
        """
    )
    fun findAllNotices(limit: Int): List<NoticeEntity>

    fun findAllByNoticeType(
        noticeType: NoticeType?,
        pageable: Pageable
    ): Page<NoticeEntity>
}

package co.yappuworld.board.infrastructure

import co.yappuworld.board.domain.model.NoticeEntity
import co.yappuworld.board.domain.model.PostEntity
import co.yappuworld.board.domain.vo.NoticeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostJpaRepository : JpaRepository<PostEntity, UUID> {

    fun findAllByNoticeType(
        noticeType: NoticeType?,
        pageable: Pageable
    ): Page<NoticeEntity>
}

package co.yappuworld.post.infrastructure

import co.yappuworld.post.domain.NoticeEntity
import co.yappuworld.post.domain.NoticeType
import co.yappuworld.post.domain.PostEntity
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PostRepository(
    private val postJpaRepository: PostJpaRepository
) {

    fun findByIdOrNull(id: UUID): PostEntity? = postJpaRepository.findByIdOrNull(id)

    fun findAllNotices(
        limit: Int,
        noticeType: NoticeType? = null,
        lastNoticeId: UUID? = null
    ): List<NoticeEntity> =
        postJpaRepository
            .findAll(Pageable.ofSize(limit)) {
                val predicates = mutableListOf<Predicate>()
                if (noticeType != null) {
                    predicates.add(path(NoticeEntity::noticeType).equal(noticeType))
                }
                if (lastNoticeId != null) {
                    predicates.add(path(NoticeEntity::getId).lessThan(lastNoticeId))
                }

                val query = select(entity(NoticeEntity::class))
                    .from(entity(NoticeEntity::class))

                if (predicates.isNotEmpty()) {
                    query.where(and(*predicates.toTypedArray()))
                }

                query.orderBy(
                    path(NoticeEntity::getId).desc()
                )
            }.filterNotNull()
}

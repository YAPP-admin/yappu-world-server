package co.yappuworld.post.infrastructure

import co.yappuworld.global.util.PageUtils.filterNotNull
import co.yappuworld.post.domain.NoticeType
import co.yappuworld.post.infrastructure.entity.NoticeEntity
import co.yappuworld.post.infrastructure.entity.PostEntity
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Transactional(readOnly = true)
class PostFindService(
    private val postRepository: PostRepository
) {

    fun findByIdOrNull(id: UUID): PostEntity? = postRepository.findByIdOrNull(id)

    fun findAllByIdIn(ids: List<UUID>): List<PostEntity> = postRepository.findAllByIdIn(ids)

    fun findAll(pageable: Pageable): Page<PostEntity> = postRepository.findAll(pageable)

    fun findAllByNoticeType(
        noticeType: NoticeType? = null,
        pageable: Pageable
    ): Page<NoticeEntity> =
        postRepository
            .findPage(pageable) {
                val query = select(entity(NoticeEntity::class))
                    .from(entity(NoticeEntity::class))

                if (noticeType != null) {
                    query.where(
                        path(NoticeEntity::noticeType).equal(noticeType)
                    )
                }

                query.orderBy(path(NoticeEntity::getId).desc())
            }.let { page ->
                PageImpl(page.content.filterNotNull(), page.pageable, page.totalElements)
            }

    fun findAllNotices(
        limit: Int,
        noticeType: NoticeType? = null,
        lastNoticeId: UUID? = null
    ): List<NoticeEntity> =
        postRepository
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

    fun findSessionNotices(
        pageable: Pageable,
        searchTitle: String? = null
    ): Page<NoticeEntity> =
        postRepository
            .findPage(pageable) {
                val predicates = mutableListOf<Predicate>()

                predicates.add(path(NoticeEntity::noticeType).equal(NoticeType.SESSION))
                searchTitle?.let {
                    predicates.add(path(NoticeEntity::title).like("%$searchTitle%"))
                }

                val query = select(entity(NoticeEntity::class))
                    .from(entity(NoticeEntity::class))

                if (predicates.isNotEmpty()) {
                    query.where(and(*predicates.toTypedArray()))
                }

                query.orderBy(path(NoticeEntity::getId).desc())
            }.filterNotNull()
}

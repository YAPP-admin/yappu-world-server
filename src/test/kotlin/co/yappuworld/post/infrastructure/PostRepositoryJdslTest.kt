package co.yappuworld.post.infrastructure

import co.yappuworld.post.domain.NoticeEntity
import co.yappuworld.post.domain.NoticeType
import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.fixture.post.PostFixture.getNoticeFixture
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@CustomDataJpaTest
class PostRepositoryJdslTest {

    @Autowired
    lateinit var postJpaRepository: PostJpaRepository

    lateinit var postRepository: PostRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @BeforeTest
    fun beforeEach() {
        postRepository = PostRepository(postJpaRepository)
    }

    @Test
    fun `KotlinJdslJpqlExecutor 테스트`() {
        val results = postJpaRepository
            .findAll(Pageable.ofSize(1)) {
                select(
                    entity(NoticeEntity::class)
                ).from(
                    entity(NoticeEntity::class)
                )
            }.filterNotNull()

        assert(results.isNotEmpty())
    }

    @Test
    @Transactional
    fun `predicate 조건이 모두 주어지면 잘 조회된다`() {
        val first = getNoticeFixture(noticeType = NoticeType.SESSION)
        val second = getNoticeFixture(noticeType = NoticeType.SESSION)
        postJpaRepository.saveAll(listOf(first, second))

        val notices = postRepository.findAllNotices(
            limit = 10,
            noticeType = NoticeType.SESSION,
            lastNoticeId = second.id
        )

        assert(notices.isNotEmpty())
        assertEquals(notices.first(), first)
    }

    @Test
    fun `predicate 조건이 null이어도 정상 조회된다`() {
        val notices = postRepository.findAllNotices(10)
        assert(notices.isNotEmpty())
    }

    @Test
    fun `predicate 조건에 따라 조회된다`() {
        val sessions = postJpaRepository.saveAll(
            listOf(
                getNoticeFixture(title = "1", noticeType = NoticeType.SESSION),
                getNoticeFixture(title = "2", noticeType = NoticeType.SESSION)
            )
        )
        val operations = postJpaRepository.saveAll(
            listOf(
                postJpaRepository.save(getNoticeFixture(title = "1", noticeType = NoticeType.OPERATION)),
                postJpaRepository.save(getNoticeFixture(title = "2", noticeType = NoticeType.OPERATION))
            )
        )
        val lastSession = postJpaRepository.save(getNoticeFixture(title = "3", noticeType = NoticeType.SESSION))
        val lastOperation = postJpaRepository.save(getNoticeFixture(title = "3", noticeType = NoticeType.OPERATION))

        entityManager.flush()
        entityManager.clear()

        postRepository.findAllNotices(2, NoticeType.SESSION).let {
            assert(it.size == 2)
            assert(it.all { n -> n.noticeType == NoticeType.SESSION })
        }

        postRepository.findAllNotices(2, NoticeType.OPERATION).let {
            assert(it.size == 2)
            assert(it.all { n -> n.noticeType == NoticeType.OPERATION })
        }

        postRepository.findAllNotices(limit = 2, lastNoticeId = lastSession.id).let {
            val operationIds = operations.map { s -> s.id }
            it.forEach { n ->
                assertContains(operationIds, n.id)
            }
        }

        postRepository.findAllNotices(2, NoticeType.SESSION, lastSession.id).let {
            val sessionIds = sessions.map { s -> s.id }
            it.forEach { n ->
                assertContains(sessionIds, n.id)
            }
        }

        postRepository.findAllNotices(2, NoticeType.OPERATION, lastOperation.id).let { notices ->
            val operationIds = operations.map { o -> o.id }
            notices.forEach { n ->
                assertContains(operationIds, n.id)
            }
        }
    }
}

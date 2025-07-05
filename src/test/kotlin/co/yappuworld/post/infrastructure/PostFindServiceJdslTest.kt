package co.yappuworld.post.infrastructure

import co.yappuworld.post.domain.NoticeType
import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.fixture.PostFixture.getNoticeEntityFixture
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@CustomDataJpaTest
class PostFindServiceJdslTest {

    @Autowired
    lateinit var postRepository: PostRepository

    lateinit var postFindService: PostFindService

    @Autowired
    lateinit var entityManager: EntityManager

    @BeforeTest
    fun beforeEach() {
        postFindService = PostFindService(postRepository)
    }

    @Test
    @Transactional
    fun `predicate 조건이 모두 주어지면 잘 조회된다`() {
        val first = getNoticeEntityFixture(noticeType = NoticeType.SESSION)
        val second = getNoticeEntityFixture(noticeType = NoticeType.SESSION)
        postRepository.saveAll(listOf(first, second))

        val notices = postFindService.findAllNotices(
            limit = 10,
            noticeType = NoticeType.SESSION,
            lastNoticeId = second.id
        )

        assert(notices.isNotEmpty())
        assertEquals(notices.first(), first)
    }

    @Test
    fun `predicate 조건에 따라 조회된다`() {
        val sessions = postRepository.saveAll(
            listOf(
                getNoticeEntityFixture(title = "1", noticeType = NoticeType.SESSION),
                getNoticeEntityFixture(title = "2", noticeType = NoticeType.SESSION)
            )
        )
        val operations = postRepository.saveAll(
            listOf(
                postRepository.save(getNoticeEntityFixture(title = "1", noticeType = NoticeType.OPERATION)),
                postRepository.save(getNoticeEntityFixture(title = "2", noticeType = NoticeType.OPERATION))
            )
        )
        val lastSession = postRepository.save(getNoticeEntityFixture(title = "3", noticeType = NoticeType.SESSION))
        val lastOperation = postRepository.save(getNoticeEntityFixture(title = "3", noticeType = NoticeType.OPERATION))

        entityManager.flush()
        entityManager.clear()

        postFindService.findAllNotices(2, NoticeType.SESSION).let {
            assert(it.size == 2)
            assert(it.all { n -> n.noticeType == NoticeType.SESSION })
        }

        postFindService.findAllNotices(2, NoticeType.OPERATION).let {
            assert(it.size == 2)
            assert(it.all { n -> n.noticeType == NoticeType.OPERATION })
        }

        postFindService.findAllNotices(limit = 2, lastNoticeId = lastSession.id).let {
            val operationIds = operations.map { s -> s.id }
            it.forEach { n ->
                assertContains(operationIds, n.id)
            }
        }

        postFindService.findAllNotices(2, NoticeType.SESSION, lastSession.id).let {
            val sessionIds = sessions.map { s -> s.id }
            it.forEach { n ->
                assertContains(sessionIds, n.id)
            }
        }

        postFindService.findAllNotices(2, NoticeType.OPERATION, lastOperation.id).let { notices ->
            val operationIds = operations.map { o -> o.id }
            notices.forEach { n ->
                assertContains(operationIds, n.id)
            }
        }
    }
}

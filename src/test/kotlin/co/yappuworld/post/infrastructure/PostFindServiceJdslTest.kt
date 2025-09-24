package co.yappuworld.post.infrastructure

import co.yappuworld.post.domain.NoticeType
import co.yappuworld.schedule.infrastructure.ScheduleRepository
import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.PostFixture.getNoticeEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import kotlin.test.assertContains
import kotlin.test.assertEquals

@CustomDataJpaTest
class PostFindServiceJdslTest @Autowired constructor(
    private val postRepository: PostRepository,
    private val scheduleRepository: ScheduleRepository,
    private val entityManager: EntityManager
) : CustomDataJpaTestFeatureSpec({

        lateinit var postFindService: PostFindService

        beforeEach {
            postFindService = PostFindService(postRepository)
        }

        feature("공지사항 목록 조회") {
            scenario("predicate 조건이 모두 주어지면 잘 조회된다") {
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

            scenario("predicate 조건에 따라 조회된다") {
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
                val lastSession = postRepository.save(
                    getNoticeEntityFixture(title = "3", noticeType = NoticeType.SESSION)
                )
                val lastOperation =
                    postRepository.save(getNoticeEntityFixture(title = "3", noticeType = NoticeType.OPERATION))

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

        feature("세션 공지사항 조회") {
            scenario("검색어가 있는 경우 매칭되는 조건에 맞추어 검색된다.") {
                postRepository.saveAndFlush(getNoticeEntityFixture(title = "우리 호빵맨~", noticeType = NoticeType.SESSION))

                postFindService.findSessionNotices(PageRequest.of(0, 5), "호빵맨").let { page ->
                    assert(page.content.isNotEmpty())
                    assertEquals(1, page.content.size)
                    assertEquals("우리 호빵맨~", page.content.first().title)
                }
            }

            scenario("세션 ID만으로 조회 된다.") {
                val session = scheduleRepository.save(getSessionEntityFixture())
                val notice = postRepository.saveAndFlush(
                    getNoticeEntityFixture(
                        title = "우리 호빵맨~",
                        noticeType = NoticeType.SESSION,
                        targetSession = session
                    )
                )

                val result = postFindService.findNoticesTargetingSession(sessionId = session.id)
                result shouldHaveSize 1
                result.first().id shouldNotBe session.id
            }
        }
    })

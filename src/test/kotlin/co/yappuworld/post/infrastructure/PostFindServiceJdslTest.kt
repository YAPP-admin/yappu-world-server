package co.yappuworld.post.infrastructure

import co.yappuworld.post.domain.NoticeType
import co.yappuworld.post.domain.NoticeType.OPERATION
import co.yappuworld.post.domain.NoticeType.SESSION
import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.PostFixture.getNoticeFixture
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired

@CustomDataJpaTest
class PostFindServiceJdslTest @Autowired constructor(
    private val postRepository: PostRepository,
    private val entityManager: EntityManager
) : CustomDataJpaTestFeatureSpec({

        val postFindService = PostFindService(postRepository)

        feature("PostFindService") {

            scenario("predicate 조건이 모두 주어지면 잘 조회된다.") {
                val first = getNoticeFixture(noticeType = NoticeType.SESSION)
                val second = getNoticeFixture(noticeType = NoticeType.SESSION)
                postRepository.saveAll(listOf(first, second))

                val notices = postFindService.findAllNotices(
                    limit = 10,
                    noticeType = NoticeType.SESSION,
                    lastNoticeId = second.id
                )

                notices.shouldNotBeEmpty()
                notices.first() shouldBe first
            }

            scenario("predicate 조건에 맞춰 조회된다.") {
                val sessions = postRepository.saveAll(
                    listOf(
                        getNoticeFixture(title = "1", noticeType = NoticeType.SESSION),
                        getNoticeFixture(title = "2", noticeType = NoticeType.SESSION)
                    )
                )
                val operations = postRepository.saveAll(
                    listOf(
                        postRepository.save(getNoticeFixture(title = "1", noticeType = OPERATION)),
                        postRepository.save(getNoticeFixture(title = "2", noticeType = OPERATION))
                    )
                )
                val lastSession = postRepository.save(getNoticeFixture(title = "3", noticeType = NoticeType.SESSION))
                val lastOperation = postRepository.save(
                    getNoticeFixture(title = "3", noticeType = OPERATION)
                )

                entityManager.flush()
                entityManager.clear()

                postFindService.findAllNotices(2, NoticeType.SESSION).let { notices ->
                    notices.shouldHaveSize(2)
                    notices.shouldForAll { it.noticeType shouldBe SESSION }
                }

                postFindService.findAllNotices(2, OPERATION).let { notices ->
                    notices.shouldHaveSize(2)
                    notices.shouldForAll { it.noticeType shouldBe OPERATION }
                }

                postFindService.findAllNotices(limit = 2, lastNoticeId = lastSession.id).let {
                    val operationIds = operations.map { s -> s.id }
                    it.forEach { n ->
                        operationIds.shouldContain(n.id)
                    }
                }

                postFindService.findAllNotices(2, NoticeType.SESSION, lastSession.id).let {
                    val sessionIds = sessions.map { s -> s.id }
                    it.forEach { n ->
                        sessionIds.shouldContain(n.id)
                    }
                }

                postFindService.findAllNotices(2, OPERATION, lastOperation.id).let { notices ->
                    val operationIds = operations.map { o -> o.id }
                    notices.forEach { n ->
                        operationIds.shouldContain(n.id)
                    }
                }
            }
        }
    })

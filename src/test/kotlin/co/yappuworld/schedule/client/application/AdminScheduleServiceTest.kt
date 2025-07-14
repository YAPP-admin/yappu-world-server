package co.yappuworld.schedule.client.application

import co.yappuworld.post.domain.NoticeType
import co.yappuworld.post.infrastructure.PostRepository
import co.yappuworld.post.infrastructure.entity.NoticeEntity
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminSimpleSessionNoticePageRequest
import co.yappuworld.schedule.infrastructure.AttendanceRepository
import co.yappuworld.schedule.infrastructure.ScheduleRepository
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.PostFixture.getNoticeEntityFixture
import co.yappuworld.support.fixture.ScheduleDtoFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class AdminScheduleServiceTest @Autowired constructor(
    private val adminScheduleService: AdminScheduleService,
    private val scheduleRepository: ScheduleRepository,
    private val noticeRepository: PostRepository,
    private val attendanceRepository: AttendanceRepository
) : SpringBootTestFeatureSpec({

        feature("세션을 수정할 때") {

            scenario("참가자 요청에 따라, attendance의 추가, 삭제가 발생한다.") {
                val session = scheduleRepository.save(getSessionEntityFixture())
                val userId1 = UUID.randomUUID()
                val userId2 = UUID.randomUUID()
                val userId3 = UUID.randomUUID()
                attendanceRepository.saveAllAndFlush(
                    listOf(userId1, userId2).map { AttendanceEntity(it, session.id) }
                )

                val request = AdminSessionUpdateRequest(
                    id = session.id,
                    name = "수정된 세션",
                    generation = 25,
                    place = "마루 180",
                    date = session.date,
                    endDate = session.endDate,
                    time = session.time,
                    endTime = session.endTime,
                    sessionType = session.sessionType,
                    sessionAttendeeIds = listOf(userId2, userId3),
                    noticeIds = emptyList()
                )
                adminScheduleService.updateSession(request)

                val result = attendanceRepository.findAll()
                result.shouldHaveSize(2)
                listOf(userId2, userId3).forEach { userId ->
                    result.any { it.userId == userId } shouldBe true
                }
            }

            scenario("공지사항 연결이 변경된다.") {
                val session = scheduleRepository.saveAndFlush(getSessionEntityFixture())
                val linkedNotices = noticeRepository.saveAllAndFlush(
                    listOf(
                        getNoticeEntityFixture(noticeType = NoticeType.SESSION, targetSession = session),
                        getNoticeEntityFixture(noticeType = NoticeType.SESSION, targetSession = session)
                    )
                )
                val newNotice = noticeRepository.saveAndFlush(getNoticeEntityFixture(noticeType = NoticeType.SESSION))

                val request = ScheduleDtoFixture.getAdminSessionUpdateRequestFixture(
                    id = session.id,
                    noticeIds = listOf(linkedNotices[0].id, newNotice.id)
                )
                adminScheduleService.updateSession(request)

                val notices = noticeRepository.findAllByIdIn(linkedNotices.map { it.id } + newNotice.id)
                (notices.single { it.id == linkedNotices[1].id } as NoticeEntity).targetSession shouldBe null
                notices
                    .filter { it.id != linkedNotices[1].id }
                    .shouldForAll { (it as NoticeEntity).targetSession?.id shouldBe session.id }
            }
        }

        feature("세션 공지사항으로 등록 가능한 공지사항 목록을 조회할 때") {
            scenario("공지사항이 다른 세션에 등록되어 있는지 여부가 표현된다.") {
                val otherSession = scheduleRepository.save(getSessionEntityFixture())
                val notice = noticeRepository.saveAndFlush(
                    getNoticeEntityFixture(noticeType = NoticeType.SESSION, targetSession = otherSession)
                )

                val result = adminScheduleService.getTargetableSessionNotices(
                    AdminSimpleSessionNoticePageRequest(page = 1, size = 10)
                )

                result.data.single { it.id == notice.id }.isSelectedByOtherSession shouldBe true
            }
        }
    })

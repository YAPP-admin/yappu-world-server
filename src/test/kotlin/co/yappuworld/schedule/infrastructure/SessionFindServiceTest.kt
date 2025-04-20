package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.AttendanceEntity
import co.yappuworld.schedule.domain.AttendanceStatus
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.UUID

@CustomDataJpaTest
class SessionFindServiceTest @Autowired constructor(
    private val scheduleRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository
) : FeatureSpec({

        extensions(SpringTestExtension(SpringTestLifecycleMode.Test))

        val sessionFindService = SessionFindService(scheduleRepository)

        feature("findSessionsWithAttendanceStatus") {

            scenario("특정 기수의 세션이 존재하지 않으면 emptylist를 반환한다.") {
                sessionFindService
                    .findSessionsWithAttendanceStatus(4, UUID.randomUUID(), LocalDateTime.now())
                    .shouldBeEmpty()
            }

            scenario("특정 기수의 세션이 존재하면 세션을 반환한다.") {
                getSessionEntityFixture(generation = 4)
                    .also { scheduleRepository.save(it) }

                sessionFindService
                    .findSessionsWithAttendanceStatus(4, UUID.randomUUID(), LocalDateTime.now())
                    .shouldNotBeEmpty()
            }

            scenario("세션은 있는데 출석이 없으면 결석이다.") {
                val session = getSessionEntityFixture(generation = 4)
                    .also { scheduleRepository.save(it) }

                val result = sessionFindService
                    .findSessionsWithAttendanceStatus(
                        4,
                        UUID.randomUUID(),
                        LocalDateTime.of(session.date.plusDays(1), session.time)
                    ).first()

                result.attendanceStatus shouldBe AttendanceStatus.ABSENT.label
                result.checkedInAt.shouldBeNull()
            }

            scenario("현재 시간보다 뒷 세션은 null 값이다.") {
                val session = getSessionEntityFixture(generation = 4)
                    .also { scheduleRepository.save(it) }

                val result = sessionFindService
                    .findSessionsWithAttendanceStatus(
                        4,
                        UUID.randomUUID(),
                        LocalDateTime.of(session.date.minusDays(1), session.time)
                    ).first()

                result.attendanceStatus.shouldBeNull()
                result.checkedInAt.shouldBeNull()
            }

            scenario("출석 상태 검증") {
                val now = LocalDateTime.of(2025, 2, 15, 0, 0)
                val userId = UUID.randomUUID()
                val status = listOf(
                    AttendanceStatus.ON_TIME,
                    AttendanceStatus.LATE,
                    AttendanceStatus.ABSENT,
                    AttendanceStatus.EARLY_CHECK_OUT,
                    AttendanceStatus.EXCUSED_ABSENCE
                )
                val sessions = mutableListOf<SessionEntity>()
                val attendances = mutableListOf<AttendanceEntity>()
                repeat(5) {
                    val session = getSessionEntityFixture(
                        generation = 4,
                        date = now.toLocalDate().minusDays(1),
                        endDate = now.toLocalDate().minusDays(1)
                    )
                    sessions.add(session)
                    attendances.add(getAttendanceFixture(status[it], userId, session.id))
                }
                scheduleRepository.saveAll(sessions)
                attendanceRepository.saveAll(attendances)

                val result = sessionFindService.findSessionsWithAttendanceStatus(4, userId, now)
                result.forEachIndexed { index, sessionWithAttendance ->
                    sessionWithAttendance.attendanceStatus shouldBe status[index].label
                }
            }
        }

        feature("findAttendancesHistory") {

            scenario("특정 기수의 세션이 존재하지 않으면 emptylist를 반환한다.") {
                sessionFindService
                    .findAttendancesHistory(4, UUID.randomUUID(), LocalDateTime.now())
                    .shouldBeEmpty()
            }

            scenario("특정 기수의 세션이 존재하면 세션을 반환한다.") {
                getSessionEntityFixture(generation = 4)
                    .also { scheduleRepository.save(it) }

                sessionFindService
                    .findAttendancesHistory(4, UUID.randomUUID(), LocalDateTime.now())
                    .shouldNotBeEmpty()
            }

            scenario("세션은 있는데 출석이 없으면 결석이다.") {
                val session = getSessionEntityFixture(generation = 4)
                    .also { scheduleRepository.save(it) }

                val result = sessionFindService
                    .findAttendancesHistory(
                        4,
                        UUID.randomUUID(),
                        LocalDateTime.of(session.date.plusDays(1), session.time)
                    ).first()

                result.attendanceStatus shouldBe AttendanceStatus.ABSENT.label
                result.checkedInAt.shouldBeNull()
            }

            scenario("현재 시간보다 뒷 세션은 조회되지 않는다.") {
                val datetime = LocalDateTime.now()
                listOf(
                    getSessionEntityFixture(generation = 4, endDate = datetime.toLocalDate().minusDays(1)),
                    getSessionEntityFixture(
                        generation = 4,
                        endDate = datetime.toLocalDate(),
                        endTime = datetime.toLocalTime().minusSeconds(1)
                    ),
                    getSessionEntityFixture(
                        generation = 4,
                        endDate = datetime.toLocalDate(),
                        endTime = datetime.toLocalTime()
                    ),
                    getSessionEntityFixture(
                        generation = 4,
                        endDate = datetime.toLocalDate(),
                        endTime = datetime.toLocalTime().plusSeconds(1)
                    ),
                    getSessionEntityFixture(generation = 4, endDate = datetime.toLocalDate().plusDays(1))
                ).also { scheduleRepository.saveAll(it) }

                sessionFindService
                    .findAttendancesHistory(
                        4,
                        UUID.randomUUID(),
                        datetime
                    ).shouldHaveSize(2)
                    .forEach {
                        it.checkedInAt.shouldBeNull()
                        it.attendanceStatus shouldBe AttendanceStatus.ABSENT.label
                    }
            }

            scenario("출석 상태 검증") {
                val now = LocalDateTime.of(2025, 2, 15, 0, 0)
                val userId = UUID.randomUUID()
                val status = listOf(
                    AttendanceStatus.ON_TIME,
                    AttendanceStatus.LATE,
                    AttendanceStatus.ABSENT,
                    AttendanceStatus.EARLY_CHECK_OUT,
                    AttendanceStatus.EXCUSED_ABSENCE
                )
                val sessions = mutableListOf<SessionEntity>()
                val attendances = mutableListOf<AttendanceEntity>()
                repeat(5) {
                    val session = getSessionEntityFixture(
                        generation = 4,
                        date = now.toLocalDate().minusDays(1),
                        endDate = now.toLocalDate().minusDays(1)
                    )
                    sessions.add(session)
                    attendances.add(getAttendanceFixture(status[it], userId, session.id))
                }
                scheduleRepository.saveAll(sessions)
                attendanceRepository.saveAll(attendances)

                val result = sessionFindService.findAttendancesHistory(4, userId, now)
                result.forEachIndexed { index, sessionWithAttendance ->
                    sessionWithAttendance.attendanceStatus shouldBe status[index].label
                }
            }
        }
    })

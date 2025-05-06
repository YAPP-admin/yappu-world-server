package co.yappuworld.schedule.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.support.environment.CustomDataJpaFeatureSpec
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.UUID

class SessionFindServiceTest @Autowired constructor(
    private val scheduleRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository
) : CustomDataJpaFeatureSpec({

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
                    attendances.add(getAttendanceEntityFixture(status[it], userId, session.id))
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
                    attendances.add(getAttendanceEntityFixture(status[it], userId, session.id))
                }
                scheduleRepository.saveAll(sessions)
                attendanceRepository.saveAll(attendances)

                val result = sessionFindService.findAttendancesHistory(4, userId, now)
                result.forEachIndexed { index, sessionWithAttendance ->
                    sessionWithAttendance.attendanceStatus shouldBe status[index].label
                }
            }
        }

        feature("findUpcomingSession") {

            scenario("예정된 세션이 없는 경우 NULL 반환") {
                shouldThrowExactly<BusinessException> {
                    sessionFindService.findUpcomingSession(25, LocalDateTime.of(2024, 12, 12, 0, 0))
                }.error shouldBe ScheduleError.NO_UPCOMING_SESSION
            }

            scenario("당일 세션이 없다면, 다음 세션 중 가장 임박한 세션이 조회된다.") {
                val generation = 25
                val now = LocalDateTime.of(2024, 12, 12, 0, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        generation = generation,
                        date = now.toLocalDate().minusDays(1),
                        endDate = now.toLocalDate().minusDays(1)
                    ),
                    getSessionEntityFixture(
                        generation = generation,
                        date = now.toLocalDate().plusDays(1),
                        endDate = now.toLocalDate().plusDays(1)
                    ),
                    getSessionEntityFixture(
                        generation = generation,
                        date = now.toLocalDate().plusDays(2),
                        endDate = now.toLocalDate().plusDays(2)
                    )
                )

                scheduleRepository.saveAllAndFlush(sessions)

                sessionFindService.findUpcomingSession(generation, now).id shouldBe
                    sessions[1].id
            }

            scenario("당일 끝나지 않은 세션이 있다면, 해당 세션이 조회된다.") {
                val generation = 25
                val now = LocalDateTime.of(2024, 12, 12, 0, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        generation = generation,
                        date = now.toLocalDate().minusDays(1),
                        endDate = now.toLocalDate().minusDays(1)
                    ),
                    getSessionEntityFixture(
                        generation = generation,
                        date = now.toLocalDate(),
                        endDate = now.toLocalDate(),
                        time = now.toLocalTime().plusHours(1),
                        endTime = now.toLocalTime().plusHours(1)
                    ),
                    getSessionEntityFixture(
                        generation = generation,
                        date = now.toLocalDate().plusDays(1),
                        endDate = now.toLocalDate().plusDays(1)
                    )
                )

                scheduleRepository.saveAllAndFlush(sessions)

                sessionFindService.findUpcomingSession(generation, now).id shouldBe
                    sessions[1].id
            }
        }
    })

package co.yappuworld.schedule.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.util.LocalDateRange
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class SessionFindServiceTest @Autowired constructor(
    private val scheduleRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository,
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext
) : CustomDataJpaTestFeatureSpec({

        val sessionFindService = SessionFindService(scheduleRepository, entityManager, context)

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
                    attendances.add(
                        getAttendanceEntityFixture(status[it], userId, session)
                            .also { attendance -> attendance.checkIn(status[it], now) }
                    )
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
                    .findAttendancesHistories(4, UUID.randomUUID(), LocalDateTime.now())
                    .shouldBeEmpty()
            }

            scenario("내가 참석자로 존재하는 세션만 조회된다.") {
                val now = LocalDateTime.of(2024, 12, 14, 0, 0)
                val user = getUserEntityFixture()
                val sessions = listOf(
                    getSessionEntityFixture(
                        generation = 4,
                        date = LocalDate.of(2024, 12, 12),
                        endDate = LocalDate.of(2024, 12, 12)
                    ),
                    getSessionEntityFixture(
                        generation = 4,
                        date = LocalDate.of(2024, 12, 13),
                        endDate = LocalDate.of(2024, 12, 13)
                    )
                ).also { scheduleRepository.saveAll(it) }
                getAttendanceEntityFixture(
                    userId = user.id,
                    session = sessions[0],
                    status = AttendanceStatus.PENDING
                ).also { attendanceRepository.saveAndFlush(it) }

                sessionFindService.findAttendancesHistories(generation = 4, userId = user.id, now = now).let {
                    it.shouldHaveSize(1)
                    it[0].id shouldBe sessions[0].id
                    it[0].attendanceStatus shouldBe AttendanceStatus.ABSENT.label
                }
            }

            scenario("종료 시간이 지나지 않은 세션에 대해, 출석을 한 경우만 노출이 된다.") {
                val now = LocalDateTime.of(2024, 12, 12, 10, 40)
                val user = getUserEntityFixture()
                val sessions = listOf(
                    getSessionEntityFixture(
                        generation = 4,
                        date = LocalDate.of(2024, 12, 12),
                        time = LocalTime.of(10, 0),
                        endDate = LocalDate.of(2024, 12, 12),
                        endTime = LocalTime.of(11, 0)
                    ),
                    getSessionEntityFixture(
                        generation = 4,
                        date = LocalDate.of(2024, 12, 12),
                        time = LocalTime.of(10, 0),
                        endDate = LocalDate.of(2024, 12, 12),
                        endTime = LocalTime.of(11, 0)
                    )
                ).also { scheduleRepository.saveAll(it) }
                sessions
                    .map { getAttendanceEntityFixture(userId = user.id, session = it) }
                    .also {
                        it[0].updateStatus(AttendanceStatus.ON_TIME)
                        attendanceRepository.saveAllAndFlush(it)
                    }

                sessionFindService.findAttendancesHistories(generation = 4, userId = user.id, now = now).let {
                    it.shouldHaveSize(1)
                    it[0].id shouldBe sessions[0].id
                    it[0].attendanceStatus shouldBe AttendanceStatus.ON_TIME.label
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
                    attendances.add(
                        getAttendanceEntityFixture(status[it], userId, session)
                            .also { attendance -> attendance.checkIn(status[it], now) }
                    )
                }
                scheduleRepository.saveAll(sessions)
                attendanceRepository.saveAll(attendances)

                val result = sessionFindService.findAttendancesHistories(4, userId, now)
                result.forEachIndexed { index, sessionWithAttendance ->
                    sessionWithAttendance.attendanceStatus shouldBe status[index].label
                }
            }
        }

        feature("findUpcomingSession") {

            scenario("예정된 세션이 없는 경우 예외 발생") {
                shouldThrowExactly<BusinessException> {
                    sessionFindService.findUpcomingSession(UUID.randomUUID(), 25, LocalDateTime.of(2024, 12, 12, 0, 0))
                }.error shouldBe ScheduleError.NO_UPCOMING_SESSION
            }

            scenario("세션 초대 현황에 따라 조회되는 세션이 다르다.") {
                val today = LocalDate.of(2024, 12, 12)
                val tomorrowSession = getSessionEntityFixture(
                    generation = 25,
                    date = today.plusDays(1),
                    endDate = today.plusDays(1)
                )
                val nextWeekSession = getSessionEntityFixture(
                    generation = 25,
                    date = today.plusDays(7),
                    endDate = today.plusDays(7)
                )
                scheduleRepository.saveAll(listOf(tomorrowSession, nextWeekSession))

                val user1 = getUserEntityFixture()
                val user2 = getUserEntityFixture()

                attendanceRepository.saveAllAndFlush(
                    listOf(
                        getAttendanceEntityFixture(
                            status = AttendanceStatus.PENDING,
                            userId = user1.id,
                            session = tomorrowSession
                        ),
                        getAttendanceEntityFixture(
                            status = AttendanceStatus.PENDING,
                            userId = user1.id,
                            session = nextWeekSession
                        ),
                        getAttendanceEntityFixture(
                            status = AttendanceStatus.PENDING,
                            userId = user2.id,
                            session = nextWeekSession
                        )
                    )
                )

                sessionFindService
                    .findUpcomingSession(
                        userId = user1.id,
                        activeGeneration = 25,
                        now = today.atStartOfDay()
                    ).id shouldBe tomorrowSession.id
                sessionFindService
                    .findUpcomingSession(
                        userId = user2.id,
                        activeGeneration = 25,
                        now = today.atStartOfDay()
                    ).id shouldBe nextWeekSession.id
            }
        }

        feature("기수와 날짜를 기준으로 세션 목록을 조회한다.") {

            scenario("파라미터로 전달된 기수 세션만 조회된다.") {
                scheduleRepository.saveAllAndFlush(
                    listOf(
                        getSessionEntityFixture(generation = 24),
                        getSessionEntityFixture(generation = 25)
                    )
                )

                sessionFindService
                    .findSessions(generation = 25)
                    .shouldHaveSize(1)
            }

            scenario("기간이 파라미터로 넘어가면, 기간 내의 세션만 조회한다.") {
                scheduleRepository.saveAllAndFlush(
                    listOf(
                        getSessionEntityFixture(date = LocalDate.of(2024, 12, 12)),
                        getSessionEntityFixture(date = LocalDate.of(2024, 12, 13)),
                        getSessionEntityFixture(date = LocalDate.of(2024, 12, 14)),
                        getSessionEntityFixture(date = LocalDate.of(2024, 12, 15))
                    )
                )

                sessionFindService
                    .findSessions(
                        range = LocalDateRange(
                            LocalDate.of(2024, 12, 13),
                            LocalDate.of(2024, 12, 14)
                        )
                    ).shouldHaveSize(2)
            }

            scenario("기수와 기간 모두 충족하는 세션만 조회된다.") {
                scheduleRepository.saveAllAndFlush(
                    listOf(
                        getSessionEntityFixture(generation = 24, date = LocalDate.of(2024, 12, 12)),
                        getSessionEntityFixture(generation = 25, date = LocalDate.of(2024, 12, 13)),
                        getSessionEntityFixture(generation = 24, date = LocalDate.of(2024, 12, 14)),
                        getSessionEntityFixture(generation = 25, date = LocalDate.of(2024, 12, 15))
                    )
                )

                sessionFindService
                    .findSessions(
                        generation = 25,
                        range = LocalDateRange(
                            LocalDate.of(2024, 12, 13),
                            LocalDate.of(2024, 12, 14)
                        )
                    ).let {
                        it.shouldHaveSize(1)
                        it.first().date shouldBe LocalDate.of(2024, 12, 13)
                    }
            }

            scenario("정렬 순서는 시작 날짜, 시간, 종료 날짜, 시간 순이다.") {
                scheduleRepository.saveAllAndFlush(
                    listOf(
                        getSessionEntityFixture(date = LocalDate.of(2024, 12, 13)),
                        getSessionEntityFixture(date = LocalDate.of(2024, 12, 15)),
                        getSessionEntityFixture(date = LocalDate.of(2024, 12, 12), time = LocalTime.of(15, 0)),
                        getSessionEntityFixture(date = LocalDate.of(2024, 12, 12), time = LocalTime.of(14, 0)),
                        getSessionEntityFixture(date = LocalDate.of(2024, 12, 14))
                    )
                )

                sessionFindService.findSessions().let {
                    it.shouldHaveSize(5)
                    it[0].date shouldBe LocalDate.of(2024, 12, 12)
                    it[0].time shouldBe LocalTime.of(14, 0)
                    it[1].date shouldBe LocalDate.of(2024, 12, 12)
                    it[2].date shouldBe LocalDate.of(2024, 12, 13)
                    it[3].date shouldBe LocalDate.of(2024, 12, 14)
                    it[4].date shouldBe LocalDate.of(2024, 12, 15)
                }
            }
        }
    })

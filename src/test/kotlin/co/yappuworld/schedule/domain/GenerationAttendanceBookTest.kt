package co.yappuworld.schedule.domain

import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.jpa.LatePassEntity
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionParticipantFixture
import co.yappuworld.support.fixture.UserFixture.getUserActivityUnitFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class GenerationAttendanceBookTest :
    FeatureSpec({
        val now = LocalDateTime.of(2024, 12, 10, 0, 0)
        val generation = 25
        val users = List(2) { getUserActivityUnitFixture(generation = generation) }
        val userActivityUnits = users.map { getUserActivityUnitFixture(userId = it.userId, generation = generation) }
        val sessions = listOf(
            getSessionEntityFixture(
                name = "1",
                date = now.toLocalDate().minusDays(2),
                endDate = now.toLocalDate().minusDays(2),
                generation = generation
            ),
            getSessionEntityFixture(
                name = "2",
                date = now.toLocalDate().minusDays(1),
                endDate = now.toLocalDate().minusDays(1),
                generation = generation
            ),
            getSessionEntityFixture(
                name = "3",
                date = now.toLocalDate().plusDays(1),
                endDate = now.toLocalDate().plusDays(1),
                generation = generation
            )
        )

        feature("임의의 데이터로 AttendanceBook을 생성한다.") {

            val sessionParticipants = listOf(
                getSessionParticipantFixture(
                    generation = generation,
                    userId = users[0].userId,
                    activityUnitId = userActivityUnits[0].activityUnitId,
                    sessionId = sessions[0].id,
                    attendanceStatus = AttendanceStatus.ON_TIME
                ),
                getSessionParticipantFixture(
                    generation = generation,
                    userId = users[0].userId,
                    activityUnitId = userActivityUnits[0].activityUnitId,
                    sessionId = sessions[1].id,
                    attendanceStatus = AttendanceStatus.LATE
                ),
                getSessionParticipantFixture(
                    generation = generation,
                    userId = users[0].userId,
                    activityUnitId = userActivityUnits[0].activityUnitId,
                    sessionId = sessions[2].id,
                    attendanceStatus = AttendanceStatus.PENDING
                ),
                getSessionParticipantFixture(
                    generation = generation,
                    userId = users[1].userId,
                    activityUnitId = userActivityUnits[1].activityUnitId,
                    sessionId = sessions[0].id,
                    attendanceStatus = AttendanceStatus.PENDING
                ),
                getSessionParticipantFixture(
                    generation = generation,
                    userId = users[1].userId,
                    activityUnitId = userActivityUnits[1].activityUnitId,
                    sessionId = sessions[1].id,
                    attendanceStatus = AttendanceStatus.ABSENT
                )
            )

            val latePassEntities = listOf(
                LatePassEntity(
                    userId = users[0].userId,
                    generation = generation
                )
            )

            scenario("AttendanceBook을 생성한다.") {
                shouldNotThrowAny {
                    GenerationAttendanceBook(
                        generation = generation,
                        userActivityUnits = userActivityUnits,
                        sessionParticipants = sessionParticipants,
                        sessions = sessions,
                        latePasses = latePassEntities,
                        now = now
                    )
                }
            }

            scenario("유저 출석 통계가 정상적으로 생성된다.") {
                val attendanceBook = GenerationAttendanceBook(
                    generation = generation,
                    userActivityUnits = userActivityUnits,
                    sessionParticipants = sessionParticipants,
                    sessions = sessions,
                    latePasses = latePassEntities,
                    now = now
                )

                attendanceBook.getUserAttendanceStatistics(users[0].userId).let {
                    it.totalSessionCount shouldBe 3
                    it.onTimeCount shouldBe 1
                    it.lateCount shouldBe 1
                    it.absentCount shouldBe 0
                    it.earlyCheckOutCount shouldBe 0
                    it.excusedAbsenceCount shouldBe 0
                    it.latePassCount shouldBe 1
                    it.totalPoint shouldBe 100
                    it.penaltyPoint shouldBe 10
                    it.bonusPoint shouldBe 10
                }

                attendanceBook.getUserAttendanceStatistics(users[1].userId).let {
                    it.totalSessionCount shouldBe 2
                    it.onTimeCount shouldBe 0
                    it.lateCount shouldBe 0
                    it.absentCount shouldBe 2
                    it.earlyCheckOutCount shouldBe 0
                    it.excusedAbsenceCount shouldBe 0
                    it.latePassCount shouldBe 0
                    it.totalPoint shouldBe 60
                    it.penaltyPoint shouldBe 40
                    it.bonusPoint shouldBe 0
                }
            }

            scenario("세션 출석 통계가 정상적으로 생성된다.") {
                val attendanceBook = GenerationAttendanceBook(
                    generation = generation,
                    userActivityUnits = userActivityUnits,
                    sessionParticipants = sessionParticipants,
                    sessions = sessions,
                    latePasses = latePassEntities,
                    now = now
                )

                attendanceBook.getSessionAttendanceStatistics(sessions[0].id).let {
                    it.totalPersonCount shouldBe 2
                    it.totalOnTimeCount shouldBe 1
                    it.totalLateCount shouldBe 0
                    it.totalAbsentCount shouldBe 1
                    it.totalEarlyCheckOutCount shouldBe 0
                    it.totalExcusedAbsenceCount shouldBe 0
                }

                attendanceBook.getSessionAttendanceStatistics(sessions[1].id).let {
                    it.totalPersonCount shouldBe 2
                    it.totalOnTimeCount shouldBe 0
                    it.totalLateCount shouldBe 1
                    it.totalAbsentCount shouldBe 1
                    it.totalEarlyCheckOutCount shouldBe 0
                    it.totalExcusedAbsenceCount shouldBe 0
                }

                attendanceBook.getSessionAttendanceStatistics(sessions[2].id).let {
                    it.totalPersonCount shouldBe 1
                    it.totalOnTimeCount shouldBe 0
                    it.totalLateCount shouldBe 0
                    it.totalAbsentCount shouldBe 0
                    it.totalEarlyCheckOutCount shouldBe 0
                    it.totalExcusedAbsenceCount shouldBe 0
                }
            }

            scenario("지각 면제권이 100점을 초과하도록 주어져도, total point는 100점을 넘지 않는다.") {
                val attendanceBook = GenerationAttendanceBook(
                    generation = generation,
                    userActivityUnits = userActivityUnits,
                    sessionParticipants = sessionParticipants,
                    sessions = sessions,
                    latePasses = List(3) {
                        LatePassEntity(
                            userId = users[0].userId,
                            generation = generation
                        )
                    },
                    now = now
                )

                attendanceBook.getUserAttendanceStatistics(users[0].userId).totalPoint shouldBe 100
            }

            scenario("출석 데이터가 정상적으로 생성된다.") {
                val attendanceBook = GenerationAttendanceBook(
                    generation = generation,
                    userActivityUnits = userActivityUnits,
                    sessionParticipants = sessionParticipants,
                    sessions = sessions,
                    latePasses = latePassEntities,
                    now = now
                )

                for (user in users) {
                    for (session in sessions) {
                        val expected = attendanceBook.getStatus(session.id, user.userId)
                        val sessionParticipant =
                            sessionParticipants.singleOrNull { it.userId == user.userId && it.sessionId == session.id }
                        val actual = when {
                            sessionParticipant == null -> null
                            sessionParticipant.isPending() && session.isFinished(now) -> AttendanceStatus.ABSENT
                            else -> sessionParticipant.attendanceStatus
                        }

                        expected shouldBe actual
                    }
                }
            }
        }
    })

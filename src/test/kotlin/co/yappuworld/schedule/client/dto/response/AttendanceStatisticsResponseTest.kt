package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.vo.AttendanceStatus.ABSENT
import co.yappuworld.schedule.domain.vo.AttendanceStatus.LATE
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ON_TIME
import co.yappuworld.schedule.domain.vo.AttendanceStatus.PENDING
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceBookFixture
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.AttendanceFixture.getAttendeeFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitFixture
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class AttendanceStatisticsResponseTest :
    FeatureSpec({

        feature("데이터 검증") {
            val generation = 25
            val user = getUserWithActivityUnitFixture(generation = generation)
            val datetime = LocalDateTime.of(2025, 3, 4, 8, 0, 0)
            val sessions = listOf(
                getSessionEntityFixture(
                    date = datetime.toLocalDate().minusDays(1),
                    endDate = datetime.toLocalDate().minusDays(1),
                    generation = generation
                ),
                getSessionEntityFixture(
                    date = datetime.toLocalDate(),
                    endDate = datetime.toLocalDate(),
                    time = datetime.toLocalTime().minusMinutes(20),
                    endTime = datetime.toLocalTime().minusMinutes(10),
                    generation = generation
                ),
                getSessionEntityFixture(
                    date = datetime.toLocalDate(),
                    endDate = datetime.toLocalDate(),
                    time = datetime.toLocalTime().minusMinutes(20),
                    endTime = datetime.toLocalTime().plusMinutes(20),
                    generation = generation
                ),
                getSessionEntityFixture(
                    date = datetime.toLocalDate(),
                    endDate = datetime.toLocalDate(),
                    time = datetime.toLocalTime().plusMinutes(10),
                    endTime = datetime.toLocalTime().plusMinutes(10),
                    generation = generation
                ),
                getSessionEntityFixture(
                    date = datetime.toLocalDate().plusDays(1),
                    endDate = datetime.toLocalDate().plusDays(1),
                    generation = generation
                )
            )

            scenario("일자, 시간에 따른 잔여 세션 수와 진행률 검증") {
                val attendances = sessions.map { getAttendanceEntityFixture(session = it, userId = user.userId) }
                AttendanceStatisticsResponse
                    .from(
                        getAttendanceBookFixture(
                            generation = generation,
                            attendees = listOf(getAttendeeFixture(user, generation)),
                            sessions = sessions,
                            attendances = attendances,
                            now = datetime
                        ).getUserAttendanceStatistics(user.userId)
                    ).let {
                        it.totalSessionCount shouldBe 5
                        it.remainingSessionCount shouldBe 3
                        it.sessionProgressRate shouldBe 40
                    }
            }

            scenario("datetime 기준으로 조회를 진행") {
                val attendances = sessions.map {
                    getAttendanceEntityFixture(
                        userId = user.userId,
                        session = it,
                        status = PENDING
                    )
                }

                AttendanceStatisticsResponse
                    .from(
                        getAttendanceBookFixture(
                            generation = generation,
                            attendees = listOf(getAttendeeFixture(user, generation)),
                            sessions = sessions,
                            attendances = attendances,
                            latePassCountByUserId = mapOf(user.userId to 0),
                            now = datetime
                        ).getUserAttendanceStatistics(user.userId)
                    ).let {
                        it.totalSessionCount shouldBe 5
                        it.remainingSessionCount shouldBe 3
                        it.sessionProgressRate shouldBe 40
                    }
            }

            scenario("모두 출석이면 출석 점수가 100점이다.") {
                val attendances = sessions
                    .subList(0, 2)
                    .map {
                        getAttendanceEntityFixture(
                            userId = user.userId,
                            session = it,
                            status = ON_TIME
                        )
                    }.union(
                        sessions.subList(2, 5).map {
                            getAttendanceEntityFixture(
                                userId = user.userId,
                                session = it,
                                status = PENDING
                            )
                        }
                    ).toList()

                AttendanceStatisticsResponse
                    .from(
                        getAttendanceBookFixture(
                            generation = generation,
                            attendees = listOf(getAttendeeFixture(user, generation)),
                            sessions = sessions,
                            attendances = attendances,
                            now = datetime
                        ).getUserAttendanceStatistics(user.userId)
                    ).let {
                        it.totalSessionCount shouldBe 5
                        it.remainingSessionCount shouldBe 3
                        it.sessionProgressRate shouldBe 40
                        it.attendancePoint shouldBe 100
                        it.attendanceCount shouldBe 2
                        it.lateCount shouldBe 0
                        it.absenceCount shouldBe 0
                    }
            }

            scenario("지각 횟수당 10점씩 깎인다.") {
                val attendances = sessions
                    .subList(0, 2)
                    .map { getAttendanceEntityFixture(status = ON_TIME, userId = user.userId, session = it) }

                repeat(2) { r ->
                    attendances[r].updateStatus(LATE)
                    AttendanceStatisticsResponse
                        .from(
                            getAttendanceBookFixture(
                                generation = generation,
                                attendees = listOf(getAttendeeFixture(user, generation)),
                                sessions = sessions,
                                attendances = attendances,
                                now = datetime
                            ).getUserAttendanceStatistics(user.userId)
                        ).let {
                            it.attendancePoint shouldBe 100 - ((r + 1) * 10)
                            it.attendanceCount shouldBe 2 - (r + 1)
                            it.lateCount shouldBe r + 1
                        }
                }
            }

            scenario("결석 횟수당 20점씩 깎인다.") {
                val attendances = sessions
                    .subList(0, 2)
                    .map { getAttendanceEntityFixture(status = ON_TIME, userId = user.userId, session = it) }

                repeat(2) { r ->
                    attendances[r].updateStatus(ABSENT)
                    AttendanceStatisticsResponse
                        .from(
                            getAttendanceBookFixture(
                                generation = generation,
                                attendees = listOf(getAttendeeFixture(user, generation)),
                                sessions = sessions,
                                attendances = attendances,
                                now = datetime
                            ).getUserAttendanceStatistics(user.userId)
                        ).let {
                            it.attendancePoint shouldBe 100 - ((r + 1) * 20)
                            it.attendanceCount shouldBe 2 - (r + 1)
                            it.absenceCount shouldBe r + 1
                        }
                }
            }

            scenario("지각 면제권 1회당 10점씩 추가된다.") {
                val attendances = sessions.subList(0, 2).map {
                    getAttendanceEntityFixture(
                        userId = user.userId,
                        session = it,
                        status = ABSENT
                    )
                }

                repeat(3) { latePassCount ->
                    AttendanceStatisticsResponse
                        .from(
                            getAttendanceBookFixture(
                                generation = generation,
                                attendees = listOf(getAttendeeFixture(user, generation)),
                                sessions = sessions,
                                attendances = attendances,
                                latePassCountByUserId = mapOf(user.userId to latePassCount),
                                now = datetime
                            ).getUserAttendanceStatistics(user.userId)
                        ).let {
                            it.latePassCount shouldBe latePassCount
                            it.attendancePoint shouldBe 60 + latePassCount * 10
                        }
                }
            }
        }
    })

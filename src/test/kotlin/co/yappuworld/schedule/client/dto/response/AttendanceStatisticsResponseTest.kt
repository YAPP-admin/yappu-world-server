package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.AttendanceStatus
import co.yappuworld.schedule.domain.AttendanceStatus.ABSENT
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class AttendanceStatisticsResponseTest :
    FeatureSpec({

        feature("데이터 검증") {
            val datetime = LocalDateTime.of(2025, 3, 4, 8, 0, 0)
            val sessions = listOf(
                getSessionEntityFixture(
                    date = datetime.toLocalDate().minusDays(1),
                    endDate = datetime.toLocalDate().minusDays(1)
                ),
                getSessionEntityFixture(
                    date = datetime.toLocalDate(),
                    endDate = datetime.toLocalDate(),
                    time = datetime.toLocalTime().minusMinutes(10),
                    endTime = datetime.toLocalTime().minusMinutes(10)
                ),
                getSessionEntityFixture(
                    date = datetime.toLocalDate(),
                    endDate = datetime.toLocalDate(),
                    time = datetime.toLocalTime(),
                    endTime = datetime.toLocalTime()
                ),
                getSessionEntityFixture(
                    date = datetime.toLocalDate(),
                    endDate = datetime.toLocalDate(),
                    time = datetime.toLocalTime().plusMinutes(10),
                    endTime = datetime.toLocalTime().plusMinutes(10)
                ),
                getSessionEntityFixture(
                    date = datetime.toLocalDate().plusDays(1),
                    endDate = datetime.toLocalDate().plusDays(1)
                )
            )

            scenario("일자, 시간에 따른 잔여 세션 수와 진행률 검증") {
                val attendances = sessions.map { getAttendanceEntityFixture(scheduleId = it.id) }
                AttendanceStatisticsResponse.of(sessions, attendances, datetime, 0).let {
                    it.totalSessionCount shouldBe 5
                    it.remainingSessionCount shouldBe 3
                    it.sessionProgressRate shouldBe 40
                }
            }

            scenario("datetime 기준으로 조회를 진행") {
                val attendances = sessions.map { getAttendanceEntityFixture(scheduleId = it.id) }
                AttendanceStatisticsResponse
                    .of(
                        sessions = sessions,
                        attendances = attendances,
                        now = datetime,
                        latePassCount = 0
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

            scenario("모두 출석이면 출석 점수가 100점이다.") {
                AttendanceStatisticsResponse
                    .of(
                        sessions = sessions,
                        attendances = sessions.map { getAttendanceEntityFixture(scheduleId = it.id) },
                        now = datetime,
                        latePassCount = 0
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
                val attendances = sessions.map { getAttendanceEntityFixture(scheduleId = it.id) }
                repeat(2) { r ->
                    attendances[r].updateStatus(AttendanceStatus.LATE)
                    AttendanceStatisticsResponse.of(sessions, attendances, datetime, 0).let {
                        it.attendancePoint shouldBe 100 - ((r + 1) * 10)
                        it.attendanceCount shouldBe 2 - (r + 1)
                        it.lateCount shouldBe r + 1
                    }
                }
            }

            scenario("결석 횟수당 20점씩 깎인다.") {
                val attendances = sessions.map { getAttendanceEntityFixture(scheduleId = it.id) }
                repeat(2) { r ->
                    attendances[r].updateStatus(ABSENT)
                    AttendanceStatisticsResponse.of(sessions, attendances, datetime, 0).let {
                        it.attendancePoint shouldBe 100 - ((r + 1) * 20)
                        it.attendanceCount shouldBe 2 - (r + 1)
                        it.absenceCount shouldBe r + 1
                    }
                }
            }

            scenario("지각 면제권 1회당 10점씩 추가된다.") {
                val attendances = sessions.map { getAttendanceEntityFixture(scheduleId = it.id, status = ABSENT) }
                repeat(3) { latePassCount ->
                    AttendanceStatisticsResponse.of(sessions, attendances, datetime, latePassCount).let {
                        it.latePassCount shouldBe latePassCount
                        it.attendancePoint shouldBe 60 + latePassCount * 10
                    }
                }
            }
        }
    })

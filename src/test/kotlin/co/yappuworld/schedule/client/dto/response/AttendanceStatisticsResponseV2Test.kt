package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.vo.AttendanceStatus.ABSENT
import co.yappuworld.schedule.domain.vo.AttendanceStatus.LATE
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ON_TIME
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceBookFixture
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.AttendanceFixture.getAttendeeFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitFixture
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class AttendanceStatisticsResponseV2Test :
    FeatureSpec({
        feature("출석 현황 상태") {
            val generation = 25
            val user = getUserWithActivityUnitFixture(generation = generation)
            val now = LocalDateTime.of(2025, 3, 4, 8, 0, 0)
            val sessions = listOf(
                getSessionEntityFixture(
                    date = now.toLocalDate().minusDays(2),
                    endDate = now.toLocalDate().minusDays(2),
                    generation = generation
                ),
                getSessionEntityFixture(
                    date = now.toLocalDate().minusDays(1),
                    endDate = now.toLocalDate().minusDays(1),
                    generation = generation
                )
            )

            scenario("출석 점수가 80점 이상이면 GOOD 상태를 반환한다.") {
                val attendances = listOf(
                    getAttendanceEntityFixture(userId = user.userId, session = sessions[0], status = ON_TIME),
                    getAttendanceEntityFixture(userId = user.userId, session = sessions[1], status = LATE)
                )

                AttendanceStatisticsResponseV2
                    .from(
                        getAttendanceBookFixture(
                            generation = generation,
                            attendees = listOf(getAttendeeFixture(user, generation)),
                            sessions = sessions,
                            attendances = attendances,
                            now = now
                        ).getUserAttendanceStatistics(user.userId)
                    ).summaryStatus shouldBe AttendanceSummaryStatus.GOOD
            }

            scenario("출석 점수가 70점 이상 80점 미만이면 CAUTION 상태를 반환한다.") {
                val attendances = listOf(
                    getAttendanceEntityFixture(userId = user.userId, session = sessions[0], status = ABSENT),
                    getAttendanceEntityFixture(userId = user.userId, session = sessions[1], status = LATE)
                )

                AttendanceStatisticsResponseV2
                    .from(
                        getAttendanceBookFixture(
                            generation = generation,
                            attendees = listOf(getAttendeeFixture(user, generation)),
                            sessions = sessions,
                            attendances = attendances,
                            now = now
                        ).getUserAttendanceStatistics(user.userId)
                    ).summaryStatus shouldBe AttendanceSummaryStatus.CAUTION
            }

            scenario("출석 점수가 70점 미만이면 INCOMPLETE 상태를 반환한다.") {
                val attendances = listOf(
                    getAttendanceEntityFixture(userId = user.userId, session = sessions[0], status = ABSENT),
                    getAttendanceEntityFixture(userId = user.userId, session = sessions[1], status = ABSENT)
                )

                AttendanceStatisticsResponseV2
                    .from(
                        getAttendanceBookFixture(
                            generation = generation,
                            attendees = listOf(getAttendeeFixture(user, generation)),
                            sessions = sessions,
                            attendances = attendances,
                            now = now
                        ).getUserAttendanceStatistics(user.userId)
                    ).summaryStatus shouldBe AttendanceSummaryStatus.INCOMPLETE
            }
        }
    })

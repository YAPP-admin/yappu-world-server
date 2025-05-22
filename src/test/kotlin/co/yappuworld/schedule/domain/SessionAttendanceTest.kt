package co.yappuworld.schedule.domain

import co.yappuworld.support.fixture.AttendanceFixture.getAttendeeFixture
import co.yappuworld.support.fixture.AttendanceFixture.getSessionAttendanceFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserActivityUnitFixture
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SessionAttendanceTest :
    FeatureSpec({

        feature("출석 가능한 지 확인") {
            val generation = 25
            val attendee = getAttendeeFixture(getUserActivityUnitFixture(generation = generation), generation)
            val session = getSessionEntityFixture(
                generation = generation,
                date = LocalDate.of(2024, 12, 12),
                time = LocalTime.of(14, 0),
                endDate = LocalDate.of(2024, 12, 13),
                endTime = LocalTime.of(10, 0)
            )

            scenario("세션 시작 20분 전보다 더 이전이면 출석할 수 없다.") {
                val now = LocalDateTime.of(2024, 12, 12, 13, 40).minusNanos(1)
                getSessionAttendanceFixture(attendee, session, null).canCheckIn(now).shouldBeFalse()
            }

            scenario("세션 시작 20분 전부터, 세션 종료 직전까지 출석할 수 있다.") {
                forAll(
                    row(LocalDateTime.of(2024, 12, 12, 13, 40)),
                    row(LocalDateTime.of(2024, 12, 13, 10, 0).minusNanos(1))
                ) { now ->
                    getSessionAttendanceFixture(attendee, session, null).canCheckIn(now).shouldBeTrue()
                }
            }

            scenario("종료 시간부터는 출석할 수 없다.") {
                val now = LocalDateTime.of(session.endDate, session.endTime)
                getSessionAttendanceFixture(attendee, session, null).canCheckIn(now).shouldBeFalse()
            }
        }
    })

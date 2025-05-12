package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.AttendanceBook
import co.yappuworld.schedule.domain.Attendee
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ABSENT
import co.yappuworld.schedule.domain.vo.AttendanceStatus.LATE
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ON_TIME
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitFixture
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class AdminAttendancesResponseTest :
    FeatureSpec({

        feature("데이터가 적절히 생성되는지 테스트한다.") {

            val generation = 25
            val now = LocalDate.of(2024, 10, 5).atStartOfDay()
            val before = now.toLocalDate().minusDays(2)
            val after = now.toLocalDate().plusDays(2)
            val sessions = listOf(
                getSessionEntityFixture(date = before, endDate = before),
                getSessionEntityFixture(date = before, endDate = before),
                getSessionEntityFixture(date = after, endDate = after)
            )
            val users = listOf(
                getUserWithActivityUnitFixture(),
                getUserWithActivityUnitFixture(),
                getUserWithActivityUnitFixture()
            )
            val attendances = listOf(
                getAttendanceEntityFixture(scheduleId = sessions[0].id, userId = users[0].userId, status = ON_TIME),
                getAttendanceEntityFixture(scheduleId = sessions[0].id, userId = users[1].userId, status = LATE),
                getAttendanceEntityFixture(scheduleId = sessions[0].id, userId = users[2].userId, status = ON_TIME),
                getAttendanceEntityFixture(scheduleId = sessions[1].id, userId = users[2].userId, status = ABSENT)
            )

            scenario("세션, 유저, 출석 정보를 기반으로 AdminAttendancesResponse를 생성한다.") {
                val response = AdminAttendancesResponse.from(
                    AttendanceBook(
                        generation = generation,
                        attendees = users.map { Attendee(it, generation) },
                        sessions = sessions,
                        attendances = attendances,
                        latePasses = emptyList(),
                        now = now
                    )
                )

                response.users shouldHaveSize 3
                response.sessions shouldHaveSize 3
                response.attendancesGroupedBySession.forEach { it.attendances.shouldHaveSize(3) }
                response.attendancesGroupedBySession[0].attendances.count { it.status == ON_TIME.label } shouldBe 2
                response.attendancesGroupedBySession[0].attendances.count { it.status == LATE.label } shouldBe 1
                response.attendancesGroupedBySession[1].attendances.count { it.status == ABSENT.label } shouldBe 3
                response.attendancesGroupedBySession[2].attendances.forAll { it.status.shouldBeNull() }
            }
        }
    })

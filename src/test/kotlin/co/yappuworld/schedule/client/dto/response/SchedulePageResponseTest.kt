package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getTaskEntityFixture
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class SchedulePageResponseTest :
    FeatureSpec({
        feature("일정 페이지 응답") {
            scenario("세션이 아닌 일정은 제외하고 세션만 변환한다.") {
                val request = SchedulePageRequest(year = 2025, month = 2)
                val now = LocalDateTime.of(2025, 2, 15, 10, 0)

                val response = SchedulePageResponse.from(
                    schedules = listOf(getSessionEntityFixture(name = "OT"), getTaskEntityFixture(name = "사전 과제")),
                    attendances = emptyList(),
                    request = request,
                    now = now
                )
                val todaySchedules = response.dates
                    .first { it.date == now.toLocalDate() }
                    .schedules

                todaySchedules.shouldHaveSize(1)
                todaySchedules.single().name shouldBe "OT"
                response.dates
                    .filter { it.date != now.toLocalDate() }
                    .flatMap { it.schedules }
                    .shouldHaveSize(0)
            }
        }
    })

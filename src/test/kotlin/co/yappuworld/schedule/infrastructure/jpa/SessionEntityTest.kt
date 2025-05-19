package co.yappuworld.schedule.infrastructure.jpa

import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase.DONE
import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase.ONGOING
import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase.PENDING
import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase.TODAY
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SessionEntityTest :
    FeatureSpec({

        feature("현재 시간에 따라 세션의 진행 단계가 변경된다.") {
            val session = getSessionEntityFixture(
                date = LocalDate.of(2024, 12, 12),
                time = LocalTime.of(12, 0),
                endDate = LocalDate.of(2024, 12, 13),
                endTime = LocalTime.of(12, 0)
            )

            scenario("세션 종료 시간 이후로 DONE") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                session.getProgressPhase(now) shouldBe DONE
            }

            scenario("세션 전날 PENDING") {
                val now = LocalDateTime.of(2024, 12, 11, 0, 0)
                session.getProgressPhase(now) shouldBe PENDING
            }

            scenario("세션이 오늘인데 시작 안 했으면 TODAY") {
                val now = LocalDateTime.of(2024, 12, 12, 0, 0)
                session.getProgressPhase(now) shouldBe TODAY
            }

            scenario("세션이 진행 중이면 ONGOING") {
                forAll(
                    row(LocalDateTime.of(2024, 12, 12, 12, 0)),
                    row(LocalDateTime.of(2024, 12, 13, 12, 0).minusNanos(1))
                ) { now ->
                    session.getProgressPhase(now) shouldBe ONGOING
                }
            }
        }
    })

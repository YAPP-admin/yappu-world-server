package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase.DONE
import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase.ONGOING
import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase.PENDING
import co.yappuworld.schedule.domain.vo.ScheduleProgressPhase.TODAY
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitsFixture
import co.yappuworld.user.domain.vo.Position
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class SessionOverviewResponseTest :
    FeatureSpec({

        val user = getUserWithActivityUnitsFixture(
            activityUnits = listOf(
                getActivityUnitFixture(generation = 25, position = Position.SERVER)
            )
        )

        feature("upcomingSessionId 확인") {
            scenario("모두 DONE이면 null") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                val response = SessionOverviewResponse.from(
                    user = user,
                    sessions = listOf(
                        getSessionEntityFixture(
                            date = now.toLocalDate().minusDays(2),
                            endDate = now.toLocalDate().minusDays(2)
                        ),
                        getSessionEntityFixture(
                            date = now.toLocalDate().minusDays(1),
                            endDate = now.toLocalDate().minusDays(1)
                        )
                    ),
                    attendances = emptyList(),
                    now = now
                )

                response.upcomingSessionId shouldBe null
            }

            scenario("ONGOING이 우선") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        date = now.toLocalDate().minusDays(1),
                        endDate = now.toLocalDate().minusDays(1)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().minusDays(1),
                        time = now.toLocalTime().minusHours(1),
                        endDate = now.toLocalDate().plusDays(1)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate(),
                        time = now.toLocalTime().plusHours(1),
                        endDate = now.toLocalDate().plusDays(1)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(1),
                        endDate = now.toLocalDate().plusDays(2)
                    )
                )
                val response = SessionOverviewResponse.from(
                    user = user,
                    sessions = sessions,
                    attendances = emptyList(),
                    now = now
                )

                response.upcomingSessionId shouldBe sessions[1].id
            }

            scenario("ONGOING이 없으면 TODAY") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        date = now.toLocalDate().minusDays(1),
                        endDate = now.toLocalDate().minusDays(1)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate(),
                        time = now.toLocalTime().plusHours(1),
                        endDate = now.toLocalDate().plusDays(1)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(1),
                        endDate = now.toLocalDate().plusDays(2)
                    )
                )

                val response = SessionOverviewResponse.from(
                    user = user,
                    sessions = sessions,
                    attendances = emptyList(),
                    now = now
                )

                response.upcomingSessionId shouldBe sessions[1].id
            }

            scenario("TODAY가 없으면 PENDING") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        date = now.toLocalDate().minusDays(1),
                        endDate = now.toLocalDate().minusDays(1)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(1),
                        endDate = now.toLocalDate().plusDays(2)
                    )
                )

                val response = SessionOverviewResponse.from(
                    user = user,
                    sessions = sessions,
                    attendances = emptyList(),
                    now = now
                )

                response.upcomingSessionId shouldBe sessions[1].id
            }
        }

        feature("정렬 확인") {

            scenario("ProgressPhase 순으로 정렬한다.") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        date = now.toLocalDate().minusDays(1),
                        time = now.toLocalTime().minusHours(1),
                        endDate = now.toLocalDate().plusDays(1)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(1),
                        endDate = now.toLocalDate().plusDays(2)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate(),
                        time = now.toLocalTime().plusHours(1),
                        endDate = now.toLocalDate().plusDays(1)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().minusDays(1),
                        endDate = now.toLocalDate().minusDays(1)
                    )
                )
                val response = SessionOverviewResponse.from(
                    user = user,
                    sessions = sessions,
                    attendances = emptyList(),
                    now = now
                )

                response.sessions[0].progressPhase shouldBe DONE.label
                response.sessions[1].progressPhase shouldBe ONGOING.label
                response.sessions[2].progressPhase shouldBe TODAY.label
                response.sessions[3].progressPhase shouldBe PENDING.label
            }

            scenario("Phase가 같으면 날짜 순이다.") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(2),
                        endDate = now.toLocalDate().plusDays(3)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(1),
                        endDate = now.toLocalDate().plusDays(2)
                    )
                )
                val response = SessionOverviewResponse.from(
                    user = user,
                    sessions = sessions,
                    attendances = emptyList(),
                    now = now
                )

                response.sessions[0].date shouldBe sessions[1].date
                response.sessions[1].date shouldBe sessions[0].date
            }

            scenario("Phase, 날짜가 같으면 시간 순이다.") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(2),
                        time = now.toLocalTime().plusHours(2),
                        endDate = now.toLocalDate().plusDays(3),
                        endTime = now.toLocalTime().plusHours(2)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(2),
                        time = now.toLocalTime().plusHours(1),
                        endDate = now.toLocalDate().plusDays(3),
                        endTime = now.toLocalTime().plusHours(2)
                    )
                )
                val response = SessionOverviewResponse.from(
                    user = user,
                    sessions = sessions,
                    attendances = emptyList(),
                    now = now
                )

                response.sessions[0].time shouldBe sessions[1].time
                response.sessions[1].time shouldBe sessions[0].time
            }

            scenario("Phase, 날짜, 시간이 같으면 종료일 순이다.") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(2),
                        time = now.toLocalTime().plusHours(1),
                        endDate = now.toLocalDate().plusDays(4),
                        endTime = now.toLocalTime().plusHours(3)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(2),
                        time = now.toLocalTime().plusHours(1),
                        endDate = now.toLocalDate().plusDays(4),
                        endTime = now.toLocalTime().plusHours(2)
                    )
                )
                val response = SessionOverviewResponse.from(
                    user = user,
                    sessions = sessions,
                    attendances = emptyList(),
                    now = now
                )

                response.sessions[0].endDate shouldBe sessions[1].endDate
                response.sessions[1].endDate shouldBe sessions[0].endDate
            }

            scenario("Phase, 날짜, 시간, 종료일이 같으면 종료시간 순이다.") {
                val now = LocalDateTime.of(2024, 12, 13, 12, 0)
                val sessions = listOf(
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(2),
                        time = now.toLocalTime().plusHours(1),
                        endDate = now.toLocalDate().plusDays(4),
                        endTime = now.toLocalTime().plusHours(3)
                    ),
                    getSessionEntityFixture(
                        date = now.toLocalDate().plusDays(2),
                        time = now.toLocalTime().plusHours(1),
                        endDate = now.toLocalDate().plusDays(4),
                        endTime = now.toLocalTime().plusHours(2)
                    )
                )
                val response = SessionOverviewResponse.from(
                    user = user,
                    sessions = sessions,
                    attendances = emptyList(),
                    now = now
                )

                response.sessions[0].endDate shouldBe sessions[1].endDate
                response.sessions[1].endDate shouldBe sessions[0].endDate
            }
        }
    })

package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitsFixture
import co.yappuworld.user.domain.vo.Position
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class SessionAttendeeTest :
    FeatureSpec({

        feature("출석할 수 있는 참가자의 자격을 검증한다.") {

            val generation = 25
            val session = getSessionEntityFixture(generation = generation)

            scenario("세션의 기수에 활동이력이 없으면 참가자로서 자격이 없다.") {
                val userWithActivityUnits = getUserWithActivityUnitsFixture(
                    activityUnits = listOf(getActivityUnitFixture(generation = generation - 1))
                )

                shouldThrowExactly<BusinessException> {
                    Attendee(
                        userWithActivityUnits = userWithActivityUnits,
                        generation = session.generation
                    )
                }.error shouldBe AttendanceError.NO_ATTENDEE_ACTIVITY_IN_GENERATION
            }

            scenario("해당 기수에 활동은 있으나, 운영진 활동이라면 참가자로서 자격이 없다.") {
                val userWithActivityUnits = getUserWithActivityUnitsFixture(
                    activityUnits = listOf(getActivityUnitFixture(generation = generation, position = Position.STAFF))
                )

                shouldThrowExactly<BusinessException> {
                    Attendee(
                        userWithActivityUnits = userWithActivityUnits,
                        generation = session.generation
                    )
                }.error shouldBe AttendanceError.NO_ATTENDEE_POSITION_ACTIVITY_IN_GENERATION
            }

            scenario("해당 기수에 활동도 있고, 참가자 직군이라면 자격이 있다.") {
                forAll(
                    row(Position.PM),
                    row(Position.DESIGN),
                    row(Position.WEB),
                    row(Position.ANDROID),
                    row(Position.IOS),
                    row(Position.FLUTTER),
                    row(Position.SERVER)
                ) { position ->
                    val userWithActivityUnits = getUserWithActivityUnitsFixture(
                        activityUnits = listOf(getActivityUnitFixture(generation = generation, position = position))
                    )

                    shouldNotThrowAny {
                        Attendee(
                            userWithActivityUnits = userWithActivityUnits,
                            generation = session.generation
                        )
                    }
                }
            }

            scenario("운영진 활동 기록이 있더라도, 참가자 직군으로 활동이 있다면 자격이 있다.") {
                forAll(
                    row(Position.PM),
                    row(Position.DESIGN),
                    row(Position.WEB),
                    row(Position.ANDROID),
                    row(Position.IOS),
                    row(Position.FLUTTER),
                    row(Position.SERVER)
                ) { position ->
                    val userWithActivityUnits = getUserWithActivityUnitsFixture(
                        activityUnits = listOf(
                            getActivityUnitFixture(generation = generation, position = position),
                            getActivityUnitFixture(generation = generation, position = Position.STAFF)
                        )
                    )

                    shouldNotThrowAny {
                        Attendee(
                            userWithActivityUnits = userWithActivityUnits,
                            generation = session.generation
                        )
                    }
                }
            }
        }
    })

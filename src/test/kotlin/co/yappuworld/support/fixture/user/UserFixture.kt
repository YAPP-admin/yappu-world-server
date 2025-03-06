package co.yappuworld.support.fixture.user

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.ActivityUnitParam
import co.yappuworld.user.domain.model.ApplicationDetails
import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.model.User
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import com.github.f4b6a3.ulid.UlidCreator
import java.util.UUID

object UserFixture {

    fun getUserFixture(
        email: String = "email@email.com",
        password: String = "password",
        name: String = "name",
        role: UserRole = UserRole.ACTIVE
    ) = User(
        email = email,
        password = password,
        name = name,
        role = role
    )

    fun getSignUpApplicationFixture(details: ApplicationDetails = getApplicationDetailsFixture()): SignUpApplication =
        SignUpApplication(details)

    fun getApplicationDetailsFixture(
        email: String = "email@email.com",
        password: String = "abcabC!!",
        name: String = "name",
        activityUnitParams: List<ActivityUnitParam> = getActivityUnitParams(),
        fcmToken: String = "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1",
        masterAlarmToggle: Boolean = true
    ): ApplicationDetails =
        ApplicationDetails(
            email,
            password,
            name,
            activityUnitParams,
            fcmToken,
            masterAlarmToggle
        )

    fun getActivityUnitParams(
        vararg activityUnitParams: ActivityUnitParam = arrayOf(
            ActivityUnitParam(1, Position.PM)
        )
    ): List<ActivityUnitParam> = activityUnitParams.toList()

    fun getActivityUnit(
        generation: Int = 25,
        position: Position = Position.SERVER,
        userId: UUID = UUID.randomUUID()
    ): ActivityUnit =
        ActivityUnit(
            generation = generation,
            position = position,
            userId = userId
        )

    fun getActivityUnits(
        vararg activityUnits: ActivityUnit = arrayOf(
            ActivityUnit(1, Position.PM, UlidCreator.getMonotonicUlid().toUuid())
        )
    ): List<ActivityUnit> = activityUnits.toList()
}

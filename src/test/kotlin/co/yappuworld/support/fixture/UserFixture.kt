package co.yappuworld.support.fixture

import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.user.domain.entity.ActivityUnitEntity
import co.yappuworld.user.domain.entity.ActivityUnitParam
import co.yappuworld.user.domain.entity.ApplicationDetails
import co.yappuworld.user.domain.entity.SignUpApplicationEntity
import co.yappuworld.user.domain.entity.UserEntity
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import java.time.LocalDateTime
import java.util.UUID

object UserFixture {

    fun getUserFixture(
        email: String = "email@email.com",
        password: String = "password",
        name: String = "name",
        role: UserRole = UserRole.ACTIVE
    ) = UserEntity(
        email = email,
        password = password,
        name = name,
        role = role
    )

    fun getSignUpApplicationFixture(
        details: ApplicationDetails = getApplicationDetailsFixture()
    ): SignUpApplicationEntity = SignUpApplicationEntity(details)

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
            EncryptUtils.encrypt(password),
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
    ): ActivityUnitEntity =
        ActivityUnitEntity(
            generation = generation,
            position = position,
            userId = userId
        )

    fun getUserWithLastActivityUnitFixture(
        userId: UUID = UUID.randomUUID(),
        email: String = "email@abc.com",
        name: String = "홍길동",
        role: UserRole = UserRole.ACTIVE,
        isActive: Boolean = true,
        createdAt: LocalDateTime = LocalDateTime.now(),
        generation: Int = 25,
        position: Position = Position.PM,
        activityUnitId: UUID = UUID.randomUUID()
    ): UserWithLastActivityUnit =
        UserWithLastActivityUnit(
            userId = userId,
            email = email,
            name = name,
            role = role,
            isActive = isActive,
            createdAt = createdAt,
            generation = generation,
            position = position,
            activityUnitId = activityUnitId
        )
}

package co.yappuworld.support.fixture

import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.UserWithActivityUnits
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.entity.ActivityUnitParam
import co.yappuworld.user.infrastructure.entity.ApplicationDetails
import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import co.yappuworld.user.infrastructure.entity.UserAlarmSettingEntity
import co.yappuworld.user.infrastructure.entity.UserDeviceEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.util.UUID

object UserFixture {

    fun getUserEntityFixture(
        email: String = "email@email.com",
        plainPassword: String = "password",
        name: String = "name",
        role: UserRole = UserRole.ACTIVE
    ) = UserEntity(
        email = email,
        password = EncryptUtils.encrypt(plainPassword),
        name = name,
        role = role
    )

    fun getSignUpApplicationEntityFixture(
        details: ApplicationDetails = getSignUpApplicationDetailsFixture()
    ): SignUpApplicationEntity = SignUpApplicationEntity(details)

    fun getSignUpApplicationEntityFixture(
        email: String = "email@email.com",
        plainPassword: String = "abcabC!!",
        name: String = "name",
        activityUnitParams: List<ActivityUnitParam> = getActivityUnitParamsFixture(),
        fcmToken: String = "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1",
        masterAlarmToggle: Boolean = true,
        status: SignUpApplicationStatus = SignUpApplicationStatus.PENDING,
        rejectReason: String? = null
    ): SignUpApplicationEntity =
        SignUpApplicationEntity(
            details = getSignUpApplicationDetailsFixture(
                email = email,
                plainPassword = plainPassword,
                name = name,
                activityUnitParams = activityUnitParams,
                fcmToken = fcmToken,
                masterAlarmToggle = masterAlarmToggle
            ),
            status = status,
            applicantEmail = email,
            rejectReason = rejectReason
        )

    fun getSignUpApplicationDetailsFixture(
        email: String = "email@email.com",
        plainPassword: String = "abcabC!!",
        name: String = "name",
        activityUnitParams: List<ActivityUnitParam> = getActivityUnitParamsFixture(),
        fcmToken: String = "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1",
        masterAlarmToggle: Boolean = true
    ): ApplicationDetails =
        ApplicationDetails(
            email,
            EncryptUtils.encrypt(plainPassword),
            name,
            activityUnitParams,
            fcmToken,
            masterAlarmToggle
        )

    fun getActivityUnitParamsFixture(
        vararg activityUnitParams: ActivityUnitParam = arrayOf(
            ActivityUnitParam(1, Position.PM)
        )
    ): List<ActivityUnitParam> = activityUnitParams.toList()

    fun getUserAlarmSettingEntityFixture(
        device: Boolean = true,
        master: Boolean = true,
        userId: UUID = UUID.randomUUID()
    ): UserAlarmSettingEntity =
        UserAlarmSettingEntity(
            userId = userId,
            device = device
        ).apply { if (!master) toggleMaster() }

    fun getUserDeviceEntityFixture(
        userId: UUID = UUID.randomUUID(),
        fcmToken: String = "fcmToken"
    ): UserDeviceEntity =
        UserDeviceEntity(
            userId = userId,
            fcmToken = fcmToken
        )

    fun getUserWithActivityUnitFixture(
        userId: UUID = UUID.randomUUID(),
        email: String = "email@abc.com",
        name: String = "홍길동",
        role: UserRole = UserRole.ACTIVE,
        generation: Int = 25,
        position: Position = Position.SERVER
    ): UserWithActivityUnit =
        UserWithActivityUnit(
            userId = userId,
            email = email,
            name = name,
            role = role,
            generation = generation,
            position = position
        )

    fun getUserWithActivityUnitsFixture(
        userId: UUID = UUID.randomUUID(),
        email: String = "email@abc.com",
        name: String = "홍길동",
        role: UserRole = UserRole.ACTIVE,
        activityUnits: List<ActivityUnit> = listOf(
            ActivityUnit(
                generation = 25,
                position = Position.SERVER,
                userId = userId
            )
        )
    ): UserWithActivityUnits =
        UserWithActivityUnits(
            userId = userId,
            email = email,
            name = name,
            role = role,
            activityUnits = activityUnits
        )

    fun getActivityUnitParamFixture(
        generation: Int = 25,
        position: Position = Position.SERVER
    ): ActivityUnitParam =
        ActivityUnitParam(
            generation = generation,
            position = position
        )

    fun getActivityUnitFixture(
        generation: Int = 23,
        position: Position = Position.SERVER,
        userId: UUID = UUID.randomUUID()
    ) = ActivityUnit(
        generation = generation,
        position = position,
        userId = userId
    )

    fun getActivityUnitEntityFixture(
        generation: Int = 23,
        position: Position = Position.SERVER,
        userId: UUID = UUID.randomUUID()
    ) = ActivityUnitEntity(
        generation = generation,
        position = position,
        userId = userId
    )
}

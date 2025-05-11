package co.yappuworld.support.fixture

import co.yappuworld.user.client.dto.request.AdminActivityUnitUpdateRequest
import co.yappuworld.user.client.dto.request.AdminUserUpdateRequest
import co.yappuworld.user.client.dto.request.LatestSignUpApplicationRequest
import co.yappuworld.user.client.dto.request.LoginRequest
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import java.util.UUID

object UserDtoFixture {

    fun getLatestSignUpApplicationApiRequestFixture(
        email: String = "abc@abc.com",
        password: String = "abcabcabC!!"
    ): LatestSignUpApplicationRequest =
        LatestSignUpApplicationRequest(
            email,
            password
        )

    fun getLoginRequest(
        email: String = "abc@abc.com",
        password: String = "abcabcabC!!"
    ) = LoginRequest(
        email,
        password
    )

    fun getAdminUserUpdateRequestFixture(
        userId: UUID = UUID.randomUUID(),
        name: String = "홍길동",
        email: String = "abc@abc.com",
        role: UserRole = UserRole.STAFF,
        activityUnits: List<AdminActivityUnitUpdateRequest> = listOf(
            getAdminActivityUnitUpdateRequest()
        ),
        phoneNumber: String? = null,
        gender: String? = null
    ): AdminUserUpdateRequest =
        AdminUserUpdateRequest(
            userId = userId,
            name = name,
            email = email,
            role = role,
            activityUnits = activityUnits,
            phoneNumber = phoneNumber,
            gender = gender
        )

    fun getAdminActivityUnitUpdateRequest(
        id: UUID = UUID.randomUUID(),
        generation: Int = 1,
        position: Position = Position.PM
    ): AdminActivityUnitUpdateRequest =
        AdminActivityUnitUpdateRequest(
            id = id,
            generation = generation,
            position = position
        )
}

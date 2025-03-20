package co.yappuworld.support.fixture.user

import co.yappuworld.user.application.dto.request.LoginRequest
import co.yappuworld.user.presentation.dto.request.LatestSignUpApplicationApiRequestDto

object UserDtoFixture {

    fun getLatestSignUpApplicationApiRequestDtoFixture(
        email: String = "abc@abc.com",
        password: String = "abcabcabC!!"
    ): LatestSignUpApplicationApiRequestDto =
        LatestSignUpApplicationApiRequestDto(
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
}

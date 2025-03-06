package co.yappuworld.support.fixture.user

import co.yappuworld.user.application.dto.request.LatestSignUpApplicationAppRequestDto
import co.yappuworld.user.presentation.dto.request.LoginApiRequestDto

object UserDtoFixture {

    fun getLatestSignUpApplicationAppRequestDtoFixture(
        email: String = "abc@abc.com",
        password: String = "abcabcabC!!"
    ): LatestSignUpApplicationAppRequestDto =
        LatestSignUpApplicationAppRequestDto(
            email,
            password
        )

    fun getLoginApiRequestDto(
        email: String = "abc@abc.com",
        password: String = "abcabcabC!!"
    ): LoginApiRequestDto =
        LoginApiRequestDto(
            email,
            password
        )
}

package co.yappuworld.support.fixture.user

import co.yappuworld.user.presentation.dto.request.LatestSignUpApplicationApiRequestDto
import co.yappuworld.user.presentation.dto.request.LoginApiRequestDto

object UserDtoFixture {

    fun getLatestSignUpApplicationApiRequestDtoFixture(
        email: String = "abc@abc.com",
        password: String = "abcabcabC!!"
    ): LatestSignUpApplicationApiRequestDto =
        LatestSignUpApplicationApiRequestDto(
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

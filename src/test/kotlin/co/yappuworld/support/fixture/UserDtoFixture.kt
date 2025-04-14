package co.yappuworld.support.fixture

import co.yappuworld.user.client.dto.request.LatestSignUpApplicationRequest
import co.yappuworld.user.client.dto.request.LoginRequest

object UserDtoFixture {

    fun getLatestSignUpApplicationApiRequestDtoFixture(
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
}

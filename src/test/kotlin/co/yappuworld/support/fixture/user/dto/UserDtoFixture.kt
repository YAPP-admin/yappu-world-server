package co.yappuworld.support.fixture.user.dto

import co.yappuworld.user.application.dto.request.LatestSignUpApplicationAppRequestDto

fun getLatestSignUpApplicationAppRequestDtoFixture(
    email: String = "abc@abc.com",
    password: String = "abcabcabC!!"
): LatestSignUpApplicationAppRequestDto {
    return LatestSignUpApplicationAppRequestDto(
        email,
        password
    )
}

package co.yappuworld.user.application

import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.user.domain.UserSignUpApplicationStatus
import co.yappuworld.user.domain.UserSignUpApplications
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import co.yappuworld.user.presentation.dto.request.ActivityUnitRequestDto
import co.yappuworld.user.presentation.dto.request.UserSignUpRequestDto
import co.yappuworld.user.presentation.vo.PositionApiType
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test

class UserAuthServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val authApplicationRepository = mockk<UserSignUpApplicationRepository>()
    private val jwtGenerator = mockk<JwtGenerator>()
    private val configInquiryComponent = mockk<ConfigInquiryComponent>()
    private val userAuthService = UserAuthService(
        userRepository,
        authApplicationRepository,
        jwtGenerator,
        configInquiryComponent
    )

    val email = "abc@abc.com"
    val request = UserSignUpRequestDto(
        email,
        "password",
        "name",
        listOf(ActivityUnitRequestDto(1, PositionApiType.PM)),
        ""
    )

    @Test
    @DisplayName("기존에 처리되지 않은 신청이 있다면 예외가 발생한다.")
    fun validateExistsPendingApplication() {
        every { authApplicationRepository.findByApplicantEmailAndStatusIn(email, any()) } returns listOf(
            UserSignUpApplications(
                UUID.randomUUID(),
                request.email,
                request.toSignUpApplication(),
                UserSignUpApplicationStatus.PENDING,
                null
            )
        )

        assertThatThrownBy { userAuthService.submitSignUpRequest(request, LocalDateTime.now()) }
            .isInstanceOf(IllegalStateException::class.java)
            .message().isEqualTo("${email}의 처리되지 않은 기존 신청이 존재합니다.")
    }

    @Test
    @DisplayName("기존 신청 중 승인된 게 있다면 예외가 발생한다.")
    fun validateExistsApprovedApplication() {
        every { authApplicationRepository.findByApplicantEmailAndStatusIn(email, any()) } returns listOf(
            UserSignUpApplications(
                UUID.randomUUID(),
                request.email,
                request.toSignUpApplication(),
                UserSignUpApplicationStatus.APPROVED,
                null
            )
        )

        assertThatThrownBy { userAuthService.submitSignUpRequest(request, LocalDateTime.now()) }
            .isInstanceOf(IllegalStateException::class.java)
            .message().isEqualTo("${email}의 기존 요청이 이미 승인되었습니다.")
    }
}

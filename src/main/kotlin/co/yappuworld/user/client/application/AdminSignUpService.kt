package co.yappuworld.user.client.application

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.user.client.application.usecase.SignUpExecutor
import co.yappuworld.user.client.dto.request.AdminSignUpApplicationPageRequest
import co.yappuworld.user.client.dto.request.AdminSignUpApplicationSearchPageRequest
import co.yappuworld.user.client.dto.request.SignUpApplicationApproveRequest
import co.yappuworld.user.client.dto.request.SignUpApplicationRejectRequest
import co.yappuworld.user.client.dto.response.AdminSignUpApplicationOverviewResponse
import co.yappuworld.user.client.dto.response.AdminSignUpApplicationResponse
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.infrastructure.SignUpApplicationFindService
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminSignUpService(
    private val signUpExecutor: SignUpExecutor,
    private val userFindService: UserFindService,
    private val signUpApplicationFindService: SignUpApplicationFindService
) {

    @Transactional(readOnly = true)
    fun getSignUpApplicationDetails(applicationId: UUID): AdminSignUpApplicationResponse =
        signUpApplicationFindService
            .findSignUpApplication(applicationId)
            .let { application ->
                when (application.status == SignUpApplicationStatus.APPROVED) {
                    true -> AdminSignUpApplicationResponse(
                        application,
                        userFindService.findUserOrNull(application.applicantEmail)
                    )
                    false -> AdminSignUpApplicationResponse(application)
                }
            }

    @Transactional(readOnly = true)
    fun getSignUpApplications(
        request: AdminSignUpApplicationPageRequest
    ): OffsetPageResponse<AdminSignUpApplicationOverviewResponse> =
        signUpApplicationFindService
            .findSignUpApplications(request.toPageRequest())
            .let { result -> OffsetPageResponse.from(result) { AdminSignUpApplicationOverviewResponse(it) } }

    @Transactional(readOnly = true)
    fun getSignUpApplicationsV2(
        request: AdminSignUpApplicationSearchPageRequest
    ): OffsetPageResponse<AdminSignUpApplicationOverviewResponse> =
        signUpApplicationFindService
            .findSignUpApplicationsV2(request, request.toPageRequest())
            .let { OffsetPageResponse.from(it) { AdminSignUpApplicationOverviewResponse(it) } }

    @Transactional
    fun approveSignUpApplication(request: SignUpApplicationApproveRequest) {
        signUpExecutor.approve(request.applicationIds, request.role)
    }

    @Transactional
    fun rejectSignUpApplication(request: SignUpApplicationRejectRequest) {
        signUpExecutor.reject(request.applicationIds, request.reason)
    }
}

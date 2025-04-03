package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.global.util.ifNotEmpty
import co.yappuworld.operation.client.dto.request.AdminSignupCodeDeleteRequest
import co.yappuworld.operation.domain.ConfigError
import co.yappuworld.operation.infrastructure.ConfigRepository
import co.yappuworld.user.client.dto.request.AdminActivityUnitUpdateRequest
import co.yappuworld.user.client.dto.request.AdminSignUpApplicationPageRequest
import co.yappuworld.user.client.dto.request.AdminSignUpCodeUpdateRequest
import co.yappuworld.user.client.dto.request.AdminUserPageRequest
import co.yappuworld.user.client.dto.request.AdminUserUpdateRequest
import co.yappuworld.user.client.dto.request.LoginRequest
import co.yappuworld.user.client.dto.request.UserRoleUpdateRequest
import co.yappuworld.user.client.dto.response.AdminSignUpApplicationOverviewResponse
import co.yappuworld.user.client.dto.response.AdminSignUpApplicationResponse
import co.yappuworld.user.client.dto.response.AdminUserDetailResponse
import co.yappuworld.user.client.dto.response.AdminUserOverviewResponse
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.UserCommandService
import co.yappuworld.user.infrastructure.UserFindService
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserAdminService(
    private val userFindService: UserFindService,
    private val userCommandService: UserCommandService,
    private val signUpApplicationRepository: SignUpApplicationRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val configRepository: ConfigRepository,
    private val jwtGenerator: JwtGenerator,
    private val userLoginPermissionChecker: UserLoginPermissionChecker,
    private val generationActiveStateManager: co.yappuworld.operation.client.application.GenerationActiveStateManager
) {

    @Transactional
    fun login(
        request: LoginRequest,
        now: LocalDateTime
    ): Token {
        val user = userFindService
            .findByEmailOrNull(request.email)
            .let { userLoginPermissionChecker.checkPermissionAndGetUser(it, request.email, request.password) }

        if (!user.role.canAccessAdminPage()) {
            throw BusinessException(UserError.NO_AUTH_FOR_ADMIN_PAGE)
        }

        return jwtGenerator.generateToken(SecurityUser.from(user), now)
    }

    @Transactional
    fun updateUserRole(request: UserRoleUpdateRequest) {
        val user = userFindService.findByIdOrNull(request.userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)

        user.updateRole(request.role)
        userCommandService.save(user)
    }

    @Transactional(readOnly = true)
    fun getUserDetail(userId: UUID): AdminUserDetailResponse {
        val user = userFindService.findByIdOrNull(userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)
        val activityUnits = activityUnitRepository.findAllByUserId(userId)
        val activeGenerationOrNull = generationActiveStateManager.getActiveGenerationOrNull()

        return AdminUserDetailResponse(user, activityUnits, activeGenerationOrNull)
    }

    @Transactional(readOnly = true)
    fun getUserOverviews(request: AdminUserPageRequest): OffsetPageResponse<AdminUserOverviewResponse> =
        userFindService
            .findAllUserWithLastActivityUnit(
                PageRequest.of(
                    request.page - 1,
                    request.size
                )
            ).let { page ->
                OffsetPageResponse(
                    data = page.content.map { AdminUserOverviewResponse(it) },
                    totalCount = page.totalElements,
                    totalPages = page.totalPages,
                    page = request.page,
                    size = request.size
                )
            }

    fun updateUserDetails(request: AdminUserUpdateRequest) {
        updateUser(request)
        handleActivityUnitRequest(request.userId, request.activityUnits)
    }

    @Transactional(readOnly = true)
    fun getSignUpApplicationDetails(applicationId: UUID): AdminSignUpApplicationResponse {
        val application = signUpApplicationRepository.findByIdOrNull(applicationId)
            ?: throw BusinessException(UserError.NOT_FOUND_SIGN_UP_APPLICATION)

        return when (application.status == SignUpApplicationStatus.APPROVED) {
            true -> AdminSignUpApplicationResponse(
                application,
                userFindService.findByEmailOrNull(application.applicantEmail)
            )
            false -> AdminSignUpApplicationResponse(application)
        }
    }

    @Transactional(readOnly = true)
    fun getSignUpApplications(
        request: AdminSignUpApplicationPageRequest
    ): OffsetPageResponse<AdminSignUpApplicationOverviewResponse> =
        signUpApplicationRepository.findAll(request.toPageRequest()).let {
            OffsetPageResponse(
                data = it.content.map { c -> AdminSignUpApplicationOverviewResponse(c) },
                totalCount = it.totalElements,
                totalPages = it.totalPages,
                page = request.page,
                size = request.size
            )
        }

    @Transactional
    fun updateSignUpCode(request: AdminSignUpCodeUpdateRequest) {
        configRepository
            .findByIdOrNull(request.role.signUpCodeKey)
            ?.apply { update(request.getPaddedCode()) }
            ?: throw BusinessException(ConfigError.CONFIG_KEY_ERROR)
    }

    @Transactional
    fun deleteSignupCode(request: AdminSignupCodeDeleteRequest) {
        configRepository.findByIdOrNull(request.role.signUpCodeKey)?.apply { update(null) }
            ?: throw BusinessException(ConfigError.CONFIG_KEY_ERROR)
    }

    private fun updateUser(request: AdminUserUpdateRequest) {
        val user = userFindService.findByIdOrNull(request.userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)

        user.updateDetails(request.name, request.email)
        userCommandService.save(user)
    }

    private fun handleActivityUnitRequest(
        userId: UUID,
        requests: List<AdminActivityUnitUpdateRequest>
    ) {
        requests
            .partition { it.id == null }
            .let { (toCreate, toUpdateOrDelete) ->
                toUpdateOrDelete.ifNotEmpty { updateOrDeleteActivityUnit(userId, it) }
                toCreate.ifNotEmpty {
                    activityUnitRepository.saveAll(
                        it.map { r -> r.toActivityUnit(userId) }
                    )
                }
            }
    }

    private fun updateOrDeleteActivityUnit(
        userId: UUID,
        requests: List<AdminActivityUnitUpdateRequest>
    ) {
        val activityUnits = activityUnitRepository
            .findAllByUserId(userId)
            .ifEmpty { return }

        val requestById = requests.associateBy { it.id }
        activityUnits
            .partition { it.id in requestById.keys }
            .let { (toUpdate, toDelete) ->
                toDelete.ifNotEmpty { units -> activityUnitRepository.deleteAllById(units.map { it.id }) }
                toUpdate.ifNotEmpty { units ->
                    units.forEach { u ->
                        requestById[u.id]?.let { request ->
                            u.updateActivityUnit(request.generation, request.position)
                        }
                    }
                    activityUnitRepository.saveAll(units)
                }
            }
    }
}

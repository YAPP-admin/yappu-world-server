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
import co.yappuworld.user.infrastructure.ActivityUnitJpaRepository
import co.yappuworld.user.infrastructure.SignUpApplicationRepository
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.ceil

@Service
class UserAdminService(
    private val userRepository: UserRepository,
    private val signUpApplicationRepository: SignUpApplicationRepository,
    private val activityUnitJpaRepository: ActivityUnitJpaRepository,
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
        val user = userRepository
            .findUserOrNullByEmail(request.email)
            .let { userLoginPermissionChecker.checkPermissionAndGetUser(it, request.email, request.password) }

        if (!user.role.canAccessAdminPage()) {
            throw BusinessException(UserError.NO_AUTH_FOR_ADMIN_PAGE)
        }

        return jwtGenerator.generateToken(SecurityUser.from(user), now)
    }

    @Transactional
    fun updateUserRole(request: UserRoleUpdateRequest) {
        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)

        user.updateRole(request.role)
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun getUserDetail(userId: UUID): AdminUserDetailResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)
        val activityUnits = activityUnitJpaRepository.findAllByUserId(userId)
        val activeGenerationOrNull = generationActiveStateManager.getActiveGenerationOrNull()

        return AdminUserDetailResponse(user, activityUnits, activeGenerationOrNull)
    }

    @Transactional(readOnly = true)
    fun getUserOverviews(request: AdminUserPageRequest): OffsetPageResponse<AdminUserOverviewResponse> {
        val userWithActivityUnit = userRepository.findUsersWithActivityUnit(
            limit = request.size,
            offset = request.page - 1
        )
        val totalCount = userRepository.count()

        return OffsetPageResponse(
            data = userWithActivityUnit.map { AdminUserOverviewResponse(it) },
            totalCount = totalCount,
            totalPages = ceil(totalCount.toDouble() / request.size).toInt(),
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
                userRepository.findUserOrNullByEmail(application.applicantEmail)
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
        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)

        user.updateDetails(request.name, request.email)
        userRepository.save(user)
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
                    activityUnitJpaRepository.saveAll(
                        it.map { r -> r.toActivityUnit(userId) }
                    )
                }
            }
    }

    private fun updateOrDeleteActivityUnit(
        userId: UUID,
        requests: List<AdminActivityUnitUpdateRequest>
    ) {
        val activityUnits = activityUnitJpaRepository
            .findAllByUserId(userId)
            .ifEmpty { return }

        val requestById = requests.associateBy { it.id }
        activityUnits
            .partition { it.id in requestById.keys }
            .let { (toUpdate, toDelete) ->
                toDelete.ifNotEmpty { units -> activityUnitJpaRepository.deleteAllById(units.map { it.id }) }
                toUpdate.ifNotEmpty { units ->
                    units.forEach { u ->
                        requestById[u.id]?.let { request ->
                            u.updateActivityUnit(request.generation, request.position)
                        }
                    }
                    activityUnitJpaRepository.saveAll(units)
                }
            }
    }
}

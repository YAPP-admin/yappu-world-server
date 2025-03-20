package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.global.util.ifNotEmpty
import co.yappuworld.operation.application.GenerationStateManager
import co.yappuworld.operation.domain.ConfigError
import co.yappuworld.operation.infrastructure.ConfigRepository
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.user.application.dto.request.AdminActivityUnitUpdateAppRequestDto
import co.yappuworld.user.application.dto.request.LoginRequest
import co.yappuworld.user.application.dto.request.AdminSignUpApplicationPageAppRequestDto
import co.yappuworld.user.application.dto.request.AdminSignUpCodeUpdateAppRequestDto
import co.yappuworld.user.application.dto.request.AdminUserPageAppRequestDto
import co.yappuworld.user.application.dto.request.AdminUserUpdateAppRequestDto
import co.yappuworld.user.application.dto.request.UserRoleUpdateAppRequestDto
import co.yappuworld.user.application.dto.response.AdminSignUpApplicationAppResponseDto
import co.yappuworld.user.application.dto.response.AdminSignUpApplicationBundleAppResponse
import co.yappuworld.user.application.dto.response.UserOverviewAppResponseDto
import co.yappuworld.user.application.dto.response.UserOverviewBundleAppResponseDto
import co.yappuworld.user.domain.vo.SignUpApplicationStatus.APPROVED
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserRepository
import co.yappuworld.user.infrastructure.UserSignUpApplicationRepository
import co.yappuworld.user.application.dto.response.AdminUserDetailResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.ceil

@Service
class UserAdminService(
    private val userRepository: UserRepository,
    private val userSignUpApplicationRepository: UserSignUpApplicationRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val configRepository: ConfigRepository,
    private val jwtGenerator: JwtGenerator,
    private val userLoginPermissionChecker: UserLoginPermissionChecker,
    private val generationRepository: GenerationRepository,
    private val generationStateManager: GenerationStateManager
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
    fun updateUserRole(request: UserRoleUpdateAppRequestDto) {
        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)

        user.updateRole(request.role)
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun getUserDetail(userId: UUID): AdminUserDetailResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)
        val activityUnits = activityUnitRepository.findAllByUserId(userId)
        val activeGenerationOrNull = generationStateManager.getActiveGenerationOrNull()

        return AdminUserDetailResponse(user, activityUnits, activeGenerationOrNull)
    }

    @Transactional(readOnly = true)
    fun getUserOverviews(request: AdminUserPageAppRequestDto): UserOverviewBundleAppResponseDto {
        val userWithActivityUnit = userRepository.findUsersWithActivityUnit(
            limit = request.limit,
            offset = request.offset
        )
        val totalCount = userRepository.count()

        return UserOverviewBundleAppResponseDto(
            data = userWithActivityUnit.map { UserOverviewAppResponseDto(it) },
            totalCount = totalCount,
            totalPages = ceil(totalCount.toDouble() / request.limit).toInt()
        )
    }

    fun updateUserDetails(request: AdminUserUpdateAppRequestDto) {
        updateUser(request)
        handleActivityUnitRequest(request.userId, request.activityUnits)
    }

    @Transactional(readOnly = true)
    fun getSignUpApplicationDetails(applicationId: UUID): AdminSignUpApplicationAppResponseDto {
        val application = userSignUpApplicationRepository.findByIdOrNull(applicationId)
            ?: throw BusinessException(UserError.NOT_FOUND_SIGN_UP_APPLICATION)

        return when (application.status == APPROVED) {
            true -> AdminSignUpApplicationAppResponseDto(
                application,
                userRepository.findUserOrNullByEmail(application.applicantEmail)
            )
            false -> AdminSignUpApplicationAppResponseDto(application)
        }
    }

    @Transactional(readOnly = true)
    fun getSignUpApplications(
        request: AdminSignUpApplicationPageAppRequestDto
    ): AdminSignUpApplicationBundleAppResponse =
        userSignUpApplicationRepository.findAll(request.toPageRequest()).let {
            AdminSignUpApplicationBundleAppResponse(it)
        }

    @Transactional
    fun updateSignUpCode(request: AdminSignUpCodeUpdateAppRequestDto) {
        val config = configRepository.findByIdOrNull(request.role.signUpCodeKey)?.apply {
            update(request.code)
        } ?: throw BusinessException(ConfigError.CONFIG_KEY_ERROR)

        configRepository.save(config)
    }

    private fun updateUser(request: AdminUserUpdateAppRequestDto) {
        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)

        user.updateDetails(request.name, request.email)
        userRepository.save(user)
    }

    private fun handleActivityUnitRequest(
        userId: UUID,
        requests: List<AdminActivityUnitUpdateAppRequestDto>
    ) {
        requests
            .partition { it.id == null }
            .let { (toCreate, toUpdateOrDelete) ->
                toUpdateOrDelete.ifNotEmpty { updateOrDeleteActivityUnit(userId, it) }
                toCreate.ifNotEmpty {
                    activityUnitRepository.saveAll(
                        it.map { r ->
                            r.toActivityUnit(userId)
                        }
                    )
                }
            }
    }

    private fun updateOrDeleteActivityUnit(
        userId: UUID,
        requests: List<AdminActivityUnitUpdateAppRequestDto>
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

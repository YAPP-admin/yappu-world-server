package co.yappuworld.user.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.global.util.ifNotEmpty
import co.yappuworld.operation.client.application.GenerationActiveStateManager
import co.yappuworld.operation.client.dto.request.AdminSignupCodeDeleteRequest
import co.yappuworld.operation.infrastructure.ConfigCommandService
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.ConfigRepository
import co.yappuworld.team.client.application.AdminTeamService
import co.yappuworld.user.client.application.usecase.UserLoginPermissionChecker
import co.yappuworld.user.client.dto.request.AdminActivityUnitUpdateRequest
import co.yappuworld.user.client.dto.request.AdminReissueTokenRequest
import co.yappuworld.user.client.dto.request.AdminSignUpCodeUpdateRequest
import co.yappuworld.user.client.dto.request.AdminUserPageRequest
import co.yappuworld.user.client.dto.request.AdminUserUpdateRequest
import co.yappuworld.user.client.dto.request.LoginRequest
import co.yappuworld.user.client.dto.request.UserRoleUpdateRequest
import co.yappuworld.user.client.dto.response.AdminUserDetailResponse
import co.yappuworld.user.client.dto.response.AdminUserOverviewResponse
import co.yappuworld.user.client.dto.response.AdminUserProfileResponse
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.ActivityUnitCommandService
import co.yappuworld.user.infrastructure.ActivityUnitFindService
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AdminUserService(
    private val userFindService: UserFindService,
    private val activityUnitFindService: ActivityUnitFindService,
    private val activityUnitCommandService: ActivityUnitCommandService,
    private val adminTeamService: AdminTeamService,
    private val configRepository: ConfigRepository,
    private val jwtGenerator: JwtGenerator,
    private val jwtResolver: JwtResolver,
    private val userLoginPermissionChecker: UserLoginPermissionChecker,
    private val generationActiveStateManager: GenerationActiveStateManager,
    private val configFindService: ConfigFindService,
    private val configCommandService: ConfigCommandService
) {

    @Transactional
    fun login(
        request: LoginRequest,
        now: LocalDateTime
    ): Token {
        val user = userFindService
            .findUserOrNull(request.email)
            .run {
                userLoginPermissionChecker.checkLoginAvailability(this, request.email, request.password)
                checkNotNull(this)
            }.also { it.checkAdminAccessibility() }

        return jwtGenerator.generateToken(SecurityUser.from(user), now)
    }

    fun reissueToken(
        request: AdminReissueTokenRequest,
        now: LocalDateTime
    ): Token {
        val userId = jwtResolver.extractUserIdFrom(request.accessToken)
        val user = userFindService.findUserOrNull(userId)
            ?: throw BusinessException(UserError.FAIL_LOGIN_NOT_FOUND_USER)

        if (!user.isActive) {
            throw BusinessException(UserError.WITHDRAWN_USER)
        }

        return jwtGenerator.generateToken(SecurityUser.from(user), now)
    }

    @Transactional
    fun updateUserRole(request: UserRoleUpdateRequest) {
        userFindService.findUser(request.userId).apply { updateRole(request.role) }
    }

    @Transactional(readOnly = true)
    fun getUserDetail(userId: UUID): AdminUserDetailResponse {
        val activityUnits = activityUnitFindService.findActivityUnits(userId)
        return AdminUserDetailResponse(
            user = userFindService.findUser(userId),
            activityUnits = activityUnits,
            activeGeneration = generationActiveStateManager.getActiveGenerationOrNull(),
            teams = adminTeamService.getActivityUnitTeams(activityUnits)
        )
    }

    @Transactional(readOnly = true)
    fun getUserOverviews(request: AdminUserPageRequest): OffsetPageResponse<AdminUserOverviewResponse> =
        userFindService
            .findAllUserWithLastActivityUnit(request)
            .let { page -> OffsetPageResponse.from(page) { AdminUserOverviewResponse(it) } }

    @Transactional
    fun updateUserDetails(request: AdminUserUpdateRequest) {
        userFindService
            .findUser(request.userId)
            .apply { updateDetails(request.name, request.role, request.email, request.gender, request.phoneNumber) }

        handleActivityUnitRequest(request.userId, request.activityUnits)
    }

    @Transactional
    fun updateSignUpCode(request: AdminSignUpCodeUpdateRequest) {
        configFindService
            .findSignUpCodeBook()
            .apply { updateSignUpCode(request.role, request.getPaddedCode()) }
            .also { configCommandService.update(it) }
    }

    @Transactional
    fun deleteSignupCode(request: AdminSignupCodeDeleteRequest) {
        configFindService
            .findSignUpCodeBook()
            .apply { initializeSignUpCode(request.role) }
            .also { configCommandService.update(it) }
    }

    @Transactional(readOnly = true)
    fun getUserProfile(userId: UUID): AdminUserProfileResponse =
        AdminUserProfileResponse(userFindService.findUserWithLastActivityUnit(userId))

    private fun handleActivityUnitRequest(
        userId: UUID,
        requests: List<AdminActivityUnitUpdateRequest>
    ) {
        requests
            .partition { it.id == null }
            .let { (toCreate, toUpdateOrDelete) ->
                toCreate.ifNotEmpty {
                    it.forEach { request ->
                        val activityUnit = activityUnitCommandService.save(request.toActivityUnit(userId))
                        adminTeamService.assignMemberToTeam(activityUnit, request.teamId)
                    }
                }
                toUpdateOrDelete.ifNotEmpty {
                    updateOrDeleteActivityUnit(userId, it)
                }
            }
    }

    private fun updateOrDeleteActivityUnit(
        userId: UUID,
        requests: List<AdminActivityUnitUpdateRequest>
    ) {
        val activityUnits = activityUnitFindService
            .findActivityUnits(userId)
            .ifEmpty { return }

        val requestById = requests.associateBy { it.id }
        activityUnits
            .partition { it.id in requestById.keys }
            .let { (toUpdate, toDelete) ->
                toDelete.ifNotEmpty { units -> activityUnitCommandService.deleteAll(units.map { it.id }) }
                toUpdate.ifNotEmpty { units ->
                    units.forEach { u ->
                        requestById[u.id]?.let { request ->
                            u.updateActivityUnit(request.generation, request.position)
                            adminTeamService.assignMemberToTeam(u, request.teamId)
                        }
                    }
                    activityUnitCommandService.saveAll(units)
                }
            }
    }
}

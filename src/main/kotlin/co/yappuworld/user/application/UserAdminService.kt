package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.application.dto.request.AdminUserPageAppRequestDto
import co.yappuworld.user.application.dto.request.UserRoleUpdateAppRequestDto
import co.yappuworld.user.application.dto.response.UserDetailsAppResponseDto
import co.yappuworld.user.application.dto.response.UserOverviewAppResponseDto
import co.yappuworld.user.application.dto.response.UserOverviewBundleAppResponseDto
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.ActivityUnitRepository
import co.yappuworld.user.infrastructure.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserAdminService(
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository
) {

    @Transactional
    fun updateUserRole(request: UserRoleUpdateAppRequestDto) {
        val user = userRepository.findByIdOrNull(request.userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)

        user.updateRole(request.role)
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun getUserDetails(userId: UUID): UserDetailsAppResponseDto {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)
        val activityUnits = activityUnitRepository.findAllByUserId(userId)

        return UserDetailsAppResponseDto(user, activityUnits)
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
            totalCount = totalCount
        )
    }
}

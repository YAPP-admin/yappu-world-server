package co.yappuworld.user.client.dto.request

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.util.StringUtils.isPhoneNumber
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AdminUserUpdateRequest(
    @param:Schema(description = "변경 대상 유저 ID")
    val userId: UUID,
    @param:Schema(description = "이름")
    @param:NotBlank(message = "유저 이름은 필수 입력 값입니다.")
    val name: String,
    @param:Schema(description = "이메일")
    @param:NotBlank(message = "유저 이메일은 필수 입력 값입니다.")
    val email: String,
    @param:Schema(description = "역할")
    val role: UserRole,
    @param:Schema(description = "활동내역")
    @param:NotEmpty(message = "유저 활동내역은 필수 입력 값입니다.")
    val activityUnits: List<AdminActivityUnitUpdateRequest>,
    @param:Schema(description = "전화번호, 요청 시에는 하이픈을 제외한 형태로", nullable = true)
    val phoneNumber: String? = null,
    @param:Schema(description = "성별", nullable = true, allowableValues = ["남", "여"])
    val gender: String? = null
) {

    fun checkRequest() {
        checkActivityUnitUpdateRequest(activityUnits)
        checkPhoneNumber()
    }

    private fun checkActivityUnitUpdateRequest(requests: List<AdminActivityUnitUpdateRequest>) {
        val hasDuplicateActivityUnit = requests
            .groupingBy { it.generation to it.position }
            .eachCount()
            .any { it.value > 1 }

        if (hasDuplicateActivityUnit) {
            throw BusinessException(UserError.DUPLICATE_ACTIVITY_UNIT)
        }
    }

    private fun checkPhoneNumber() {
        phoneNumber?.let {
            if (it.isPhoneNumber().not()) throw BusinessException(UserError.WRONG_PHONE_NUMBER)
        }
    }
}

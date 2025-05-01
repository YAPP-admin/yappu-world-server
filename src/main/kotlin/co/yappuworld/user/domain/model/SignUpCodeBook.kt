package co.yappuworld.user.domain.model

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.ConfigEntity
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole

class SignUpCodeBook(
    configs: List<ConfigEntity>
) {

    private val codeByUserRole: MutableMap<UserRole, String?>
    private val codes: List<String>
        get() = codeByUserRole.values.filterNotNull()

    init {
        val codeByUserRole = mutableMapOf<UserRole, String?>()
        val codeById = configs.associateBy { it.id }

        UserRole.entries.forEach { entry ->
            val config = codeById[entry.signUpCodeKey]
                ?: throw BusinessException(UserError.SIGN_UP_CODE_UNREGISTERED)

            codeByUserRole[entry] = checkValue(config.value)
        }

        this.codeByUserRole = codeByUserRole
        checkDuplicateValue()
    }

    fun updateSignUpCode(
        userRole: UserRole,
        code: String
    ) {
        if (codes.contains(code)) {
            throw BusinessException(UserError.CANNOT_UPDATE_EXISTS_SIGN_UP_CODE)
        }

        codeByUserRole[userRole] = checkValue(code)
    }

    fun initializeSignUpCode(userRole: UserRole) {
        codeByUserRole[userRole] = null
    }

    fun getCode(userRole: UserRole): String? = codeByUserRole[userRole]

    fun decideRole(code: String): UserRole =
        UserRole.entries.singleOrNull { codeByUserRole[it] == code }
            ?: throw BusinessException(UserError.WRONG_SIGN_UP_CODE)

    private fun checkValue(value: String?): String? {
        if (value != null && value.length != 6) {
            throw BusinessException(UserError.INVALID_SIGN_UP_CODE)
        }

        return value
    }

    private fun checkDuplicateValue() {
        if (codes.size != codes.distinct().size) {
            throw BusinessException(UserError.SIGN_UP_CODE_DUPLICATED)
        }
    }
}

package co.yappuworld.user.domain.model

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("sign_up_application")
class SignUpApplication(
    val applicantEmail: String,
    val details: ApplicationDetails,
    status: SignUpApplicationStatus,
    rejectReason: String?
) : BaseEntity() {

    var status: SignUpApplicationStatus = status
        private set
    var rejectReason: String? = rejectReason
        private set

    constructor(application: ApplicationDetails) : this(
        application.email,
        application,
        SignUpApplicationStatus.PENDING,
        null
    )

    fun approve() {
        this.status = SignUpApplicationStatus.APPROVED
    }

    fun reject(reason: String) {
        this.status = SignUpApplicationStatus.REJECTED
        this.rejectReason = reason
    }

    fun toUser(role: UserRole): User = this.details.toUser(role)

    fun checkPassword(password: String) {
        if (!EncryptUtils.isMatch(password, details.password)) {
            throw BusinessException(UserError.MISMATCH_REQUEST_AND_SIGN_UP_APPLICATION)
        }
    }

    fun toActivityUnits(userId: UUID): List<ActivityUnit> =
        this.details.activityUnits.map {
            it.toActivityUnit(userId)
        }

    fun getFcmToken(): String = details.fcmToken

    fun getDeviceAlarmToggle(): Boolean = this.details.deviceAlarmToggle
}

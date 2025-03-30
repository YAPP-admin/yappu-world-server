package co.yappuworld.user.domain.model

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.ApplicationDetailsConverter
import co.yappuworld.global.persistence.BaseJpaEntity
import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity(name = "sign_up_applications")
@Table
class SignUpApplicationEntity(
    val applicantEmail: String,
    @Column(columnDefinition = "JSON")
    @Convert(converter = ApplicationDetailsConverter::class)
    val details: ApplicationDetails,
    status: SignUpApplicationStatus,
    rejectReason: String?
) : BaseJpaEntity() {

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

    fun toUser(role: UserRole): UserEntity = this.details.toUser(role)

    fun checkPassword(password: String) {
        if (!EncryptUtils.isMatch(password, details.password)) {
            throw BusinessException(UserError.MISMATCH_REQUEST_AND_SIGN_UP_APPLICATION)
        }
    }

    fun toActivityUnits(userId: UUID): List<ActivityUnitEntity> =
        this.details.activityUnits.map {
            it.toActivityUnit(userId)
        }

    fun getFcmToken(): String = details.fcmToken

    fun getDeviceAlarmToggle(): Boolean = this.details.deviceAlarmToggle
}

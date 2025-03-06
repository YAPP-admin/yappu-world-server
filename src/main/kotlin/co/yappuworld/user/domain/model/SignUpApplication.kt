package co.yappuworld.user.domain.model

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("sign_up_application")
class SignUpApplication private constructor(
    @Id
    @JvmField
    val id: UUID,
    val applicantEmail: String,
    val details: ApplicationDetails,
    status: SignUpApplicationStatus,
    rejectReason: String?
) : BaseEntity(),
    Persistable<UUID> {

    var status: SignUpApplicationStatus = status
        private set
    var rejectReason: String? = rejectReason
        private set

    constructor(application: ApplicationDetails) : this(
        UlidCreator.getMonotonicUlid().toUuid(),
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
        if (details.password != password) {
            throw BusinessException(UserError.MISMATCH_REQUEST_AND_SIGN_UP_APPLICATION)
        }
    }

    override fun getId(): UUID = this.id

    override fun isNew(): Boolean = !isCreatedAtInitialized()

    fun toActivityUnits(userId: UUID): List<ActivityUnit> =
        this.details.activityUnits.map {
            it.toActivityUnit(userId)
        }

    fun getFcmToken(): String = details.fcmToken

    fun getDeviceAlarmToggle(): Boolean = this.details.deviceAlarmToggle
}

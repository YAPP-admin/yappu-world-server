package co.yappuworld.user.domain.model

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.domain.vo.UserSignUpApplicationStatus
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
    status: UserSignUpApplicationStatus,
    rejectReason: String?
) : BaseEntity(), Persistable<UUID> {

    var status: UserSignUpApplicationStatus = status
        private set
    var rejectReason: String? = rejectReason
        private set

    constructor(application: ApplicationDetails) : this(
        UlidCreator.getMonotonicUlid().toUuid(),
        application.email,
        application,
        UserSignUpApplicationStatus.PENDING,
        null
    )

    fun approve() {
        this.status = UserSignUpApplicationStatus.APPROVED
    }

    fun reject(reason: String) {
        this.status = UserSignUpApplicationStatus.REJECTED
        this.rejectReason = reason
    }

    fun toUser(role: UserRole): User {
        return this.details.toUser(role)
    }

    fun checkPassword(password: String) {
        if (details.password != password) {
            throw BusinessException(UserError.MISMATCH_REQUEST_AND_SIGN_UP_APPLICATION)
        }
    }

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !isCreatedAtInitialized()
    }

    fun toActivityUnits(userId: UUID): List<ActivityUnit> {
        return this.details.activityUnits.map {
            it.toActivityUnit(userId)
        }
    }

    fun getFcmToken(): String {
        return details.fcmToken
    }
}

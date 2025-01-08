package co.yappuworld.user.domain

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.global.vo.UserRole
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
    val applicantDetails: SignUpApplicantDetails,
    var status: UserSignUpApplicationStatus,
    var rejectReason: String?
) : BaseEntity(), Persistable<UUID> {

    constructor(application: SignUpApplicantDetails) : this(
        UlidCreator.getMonotonicUlid().toUuid(),
        application.email,
        application,
        UserSignUpApplicationStatus.PENDING,
        null
    )

    fun approve(role: UserRole) {
        this.status = UserSignUpApplicationStatus.APPROVED
    }

    fun reject(reason: String) {
        this.status = UserSignUpApplicationStatus.REJECTED
        this.rejectReason = reason
    }

    fun toUser(role: UserRole): User {
        return this.applicantDetails.toUser(role)
    }

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !isCreatedAtInitialized()
    }
}

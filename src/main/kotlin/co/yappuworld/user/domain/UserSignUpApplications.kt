package co.yappuworld.user.domain

import co.yappuworld.global.persistence.BaseEntity
import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("user_sign_up_applications")
class UserSignUpApplications(
    @Id
    @JvmField
    val id: UUID,
    val applicantEmail: String,
    val application: UserSignUpApplication,
    val status: UserSignUpApplicationStatus,
    val rejectReason: String?
) : BaseEntity(), Persistable<UUID> {

    constructor(application: UserSignUpApplication) : this(
        UlidCreator.getMonotonicUlid().toUuid(),
        application.email,
        application,
        UserSignUpApplicationStatus.PENDING,
        null
    )

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !isCreatedAtInitialized()
    }
}

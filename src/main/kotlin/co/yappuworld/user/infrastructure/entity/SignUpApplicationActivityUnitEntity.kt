package co.yappuworld.user.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.user.domain.vo.Position
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "sign_up_application_activity_unit")
class SignUpApplicationActivityUnitEntity(
    @Column(name = "application_id")
    val applicationId: UUID,
    @Column(name = "generation")
    val generation: Int,
    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    val position: Position
) : BaseEntity()

package co.yappuworld.post.domain

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "posts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
abstract class PostEntity : BaseEntity() {

    protected var isActive: Boolean = true
    abstract val title: String
    abstract val content: String
    abstract val contentSummary: String
    abstract val writerId: UUID
}

package co.yappuworld.global.persistence

import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import java.time.LocalDateTime
import java.util.UUID

abstract class BaseEntity : Persistable<UUID> {

    @Id
    @JvmField
    protected var id: UUID = UlidCreator.getMonotonicUlid().toUuid()

    @CreatedDate
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    lateinit var updatedAt: LocalDateTime

    protected fun isCreatedAtInitialized(): Boolean = ::createdAt.isInitialized

    override fun getId(): UUID = this.id

    override fun isNew(): Boolean = !isCreatedAtInitialized()
}

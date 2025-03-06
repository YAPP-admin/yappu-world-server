package co.yappuworld.board.domain.model

import co.yappuworld.board.domain.vo.PostType
import co.yappuworld.board.domain.vo.Writer
import co.yappuworld.global.persistence.BaseEntity
import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("boards")
abstract class Post :
    BaseEntity(),
    Persistable<UUID> {

    @Id
    @JvmField
    protected var id: UUID = UlidCreator.getMonotonicUlid().toUuid()

    protected var isActive: Boolean = true
    abstract val postType: PostType
    abstract val title: String
    abstract val content: String
    abstract val writer: Writer

    override fun getId(): UUID = this.id

    override fun isNew(): Boolean = !isCreatedAtInitialized()

    fun delete() {
        this.isActive = false
    }
}

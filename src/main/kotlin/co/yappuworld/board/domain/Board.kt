package co.yappuworld.board.domain

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.user.domain.vo.UserRole
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table(name = "board")
data class Board(
    @Id
    @JvmField
    val id: UUID,
    val boardType: String,
    val noticeType: String? = null,
    val title: String,
    val content: String,
    val displayTarget: String?,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
    val writer: Writer,
    val isActive: Boolean = true
) : BaseEntity(), Persistable<UUID> {
    override fun getId() = id

    override fun isNew() = !isCreatedAtInitialized()

    fun filterInfo(userRole: UserRole): Board {
        return if (userRole != UserRole.ADMIN) {
            this.copy(displayTarget = null)
        } else {
            this
        }
    }
}

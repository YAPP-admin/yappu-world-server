package co.yappuworld.board.domain

import co.yappuworld.global.persistence.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
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
    val displayTarget: String,
    val writer: Writer,
    val isActive: Boolean = true,
) : BaseEntity(), Persistable<UUID> {
    override fun getId() = id

    override fun isNew() = !isCreatedAtInitialized()
}

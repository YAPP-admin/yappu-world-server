package co.yappuworld.board.domain.model

import co.yappuworld.board.domain.vo.BoardType
import co.yappuworld.board.domain.vo.NoticeType
import co.yappuworld.board.domain.vo.Writer
import co.yappuworld.global.persistence.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "board")
data class Board(
    @Id
    @JvmField
    val id: UUID,
    val boardType: BoardType,
    val noticeType: NoticeType? = null,
    val title: String,
    val content: String,
    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
    val writer: Writer,
    val isActive: Boolean = true
) : BaseEntity(),
    Persistable<UUID> {
    override fun getId() = id

    override fun isNew() = !isCreatedAtInitialized()
}

package co.yappuworld.board.domain.model

import co.yappuworld.board.domain.vo.BoardType
import co.yappuworld.board.domain.vo.NoticeType
import co.yappuworld.board.domain.vo.Writer
import co.yappuworld.global.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table

@Table(name = "boards")
data class Board(
    val boardType: BoardType,
    val noticeType: NoticeType? = null,
    val title: String,
    val content: String,
    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
    val writer: Writer,
    val isActive: Boolean = true
) : BaseEntity()

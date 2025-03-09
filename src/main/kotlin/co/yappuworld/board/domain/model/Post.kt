package co.yappuworld.board.domain.model

import co.yappuworld.board.domain.vo.PostType
import co.yappuworld.board.domain.vo.Writer
import co.yappuworld.global.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table

@Table("boards")
abstract class Post : BaseEntity() {

    protected var isActive: Boolean = true
    abstract val postType: PostType
    abstract val title: String
    abstract val content: String
    abstract val writer: Writer

    fun delete() {
        this.isActive = false
    }
}

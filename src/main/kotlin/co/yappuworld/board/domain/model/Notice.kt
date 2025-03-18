package co.yappuworld.board.domain.model

import co.yappuworld.board.domain.vo.NoticeType
import co.yappuworld.board.domain.vo.PostType
import co.yappuworld.board.domain.vo.Writer
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("boards")
class Notice(
    override var title: String,
    override var content: String,
    contentSummary: String,
    writer: Writer,
    noticeType: NoticeType
) : Post() {

    var contentSummary: String = contentSummary
        private set

    var noticeType: NoticeType = noticeType
        private set

    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
    override var writer: Writer = writer

    @Column(value = "board_type")
    override var postType: PostType = PostType.NOTICE

    fun withId(id: UUID): Notice =
        Notice(
            title = title,
            content = content,
            writer = writer,
            noticeType = noticeType,
            contentSummary = contentSummary
        ).apply { this.id = id }
}

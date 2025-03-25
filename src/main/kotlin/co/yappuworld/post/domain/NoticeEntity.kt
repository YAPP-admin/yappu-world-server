package co.yappuworld.post.domain

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.util.UUID

@Entity
@DiscriminatorValue(value = "NOTICE")
class NoticeEntity(
    override var title: String,
    override var content: String,
    override var writerId: UUID,
    override var contentSummary: String,
    noticeType: NoticeType
) : PostEntity() {

    @Enumerated(EnumType.STRING)
    var noticeType: NoticeType = noticeType
        private set

    fun update(
        title: String,
        content: String,
        contentSummary: String,
        noticeType: NoticeType
    ) {
        this.title = title
        this.content = content
        this.contentSummary = contentSummary
        this.noticeType = noticeType
    }
}

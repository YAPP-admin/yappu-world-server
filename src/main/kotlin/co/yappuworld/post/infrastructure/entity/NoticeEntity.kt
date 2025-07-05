package co.yappuworld.post.infrastructure.entity

import co.yappuworld.post.domain.NoticeType
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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

    @ManyToOne
    @JoinColumn(name = "session_id")
    var targetSession: SessionEntity? = null

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

    fun targetSession(session: SessionEntity) {
        require(noticeType == NoticeType.SESSION) { "세션 공지사항만 대상 세션이 존재할 수 있습니다." }
        this.targetSession = session
    }

    fun detachSession() {
        this.targetSession = null
    }
}

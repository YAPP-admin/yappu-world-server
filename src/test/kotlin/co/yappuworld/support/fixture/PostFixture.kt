package co.yappuworld.support.fixture

import co.yappuworld.post.domain.NoticeEntity
import co.yappuworld.post.domain.NoticeType
import com.github.f4b6a3.ulid.UlidCreator
import java.util.UUID

object PostFixture {

    fun getNoticeFixture(
        title: String = "title",
        content: String = "content",
        writerId: UUID = UlidCreator.getMonotonicUlid().toUuid(),
        contentSummary: String = "contentSummary",
        noticeType: NoticeType = NoticeType.SESSION
    ): NoticeEntity =
        NoticeEntity(
            title = title,
            content = content,
            writerId = writerId,
            contentSummary = contentSummary,
            noticeType = noticeType
        )
}

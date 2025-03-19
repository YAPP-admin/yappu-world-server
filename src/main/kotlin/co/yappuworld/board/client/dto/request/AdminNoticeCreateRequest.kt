package co.yappuworld.board.client.dto.request

import co.yappuworld.board.domain.vo.NoticeType
import org.hibernate.validator.constraints.Length

data class AdminNoticeCreateRequest(
    val type: NoticeType,
    @field:Length(max = 50, message = "제목은 50자 이하여야 합니다.")
    val title: String,
    @field:Length(max = 5000, message = "내용은 5000자 이하여야 합니다.")
    val content: String,
    val plainContent: String
)

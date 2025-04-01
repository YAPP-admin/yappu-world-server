package co.yappuworld.post.client.dto.request

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class AdminNoticeDeleteRequest(
    @Schema(description = "삭제할 ID 목록")
    val noticeIds: List<UUID>
) {

    @JsonIgnore
    val size: Int = noticeIds.toSet().size
}

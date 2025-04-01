package co.yappuworld.post.client.presentation

import co.yappuworld.post.client.dto.request.NoticePageRequest
import co.yappuworld.post.client.dto.response.NoticeResponse
import co.yappuworld.post.client.dto.response.NoticeOverviewResponse
import co.yappuworld.global.response.CursorPageResponse
import co.yappuworld.global.response.SuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

@Tag(name = "게시판 API", description = "공지사항, 자유게시판")
interface NoticeApi {

    @Operation(summary = "공지사항 리스트 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "공지사항 리스트 조회 결과",
                                value = """
                                    {
                                        "data": {
                                            "data": [
                                                {
                                                    "notice": {
                                                        "id": "d3549494-fa3a-11ef-ba22-0242ac120002",
                                                        "createdAt": "2025-03-06",
                                                        "title": "제목입니다5",
                                                        "content": "## 안녕하세요 만나서 반갑습니다",
                                                        "noticeType": "OPERATION"
                                                    },
                                                    "writer": {
                                                        "id": "01954809-38fd-1268-e0d6-d3fda39f6b4c",
                                                        "name": "홍길동",
                                                        "activityUnitGeneration": 1,
                                                        "activityUnitPosition": {
                                                            "name": "PM",
                                                            "label": "PM"
                                                        }
                                                    }
                                                }
                                            ],
                                            "lastCursor": "d3549494-fa3a-11ef-ba22-0242ac120002",
                                            "limit": 1,
                                            "hasNext": true
                                        },
                                        "isSuccess": true
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/posts/notices")
    fun getNotices(
        @ParameterObject request: NoticePageRequest
    ): ResponseEntity<SuccessResponse<CursorPageResponse<NoticeOverviewResponse, UUID>>>

    @Operation(summary = "공지사항 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "공지사항 상세 조회",
                                value = """
                                    {
                                        "data": {
                                            "notice": {
                                                "id": "ad8750bf-fa3e-11ef-ba22-0242ac120002",
                                                "title": "제목입니다5",
                                                "content": "## 안녕하세요 만나서 반갑습니다",
                                                "createdAt": "2025-03-06",
                                                "noticeType": "OPERATION"
                                            },
                                            "writer": {
                                                "id": "01954809-38fd-1268-e0d6-d3fda39f6b4c",
                                                "name": "홍길동",
                                                "activityUnitGeneration": 1,
                                                "activityUnitPosition": {
                                                    "name": "PM",
                                                    "label": "PM"
                                                }
                                            }
                                        },
                                        "isSuccess": true
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                description = "조회 실패.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "해당 게시물이 존재하지 않습니다.",
                                value = """
                                    {
                                        "isSuccess": false,
                                        "errorCode": "BRD_0001",
                                        "message": "게시글이 존재하지 않습니다."
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/posts/notices/{noticeId}")
    fun getNotice(
        @PathVariable noticeId: UUID
    ): ResponseEntity<SuccessResponse<NoticeResponse>>
}

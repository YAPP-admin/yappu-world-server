package co.yappuworld.board.client.presentation

import co.yappuworld.board.client.presentation.dto.response.BoardResponse
import co.yappuworld.global.response.SuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "공지사항 API", description = "공지사항 기능")
interface BoardApi {

    @Operation(
        summary = "공지사항 리스트 조회",
        parameters = [
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "page",
                required = true,
                schema = Schema(type = "Int")
            ),
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "size",
                required = true,
                schema = Schema(type = "Int")
            ),
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "noticeType",
                required = false,
                schema = Schema(type = "string", description = "(운영, 세션)")
            )
        ]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "공지사항 리스트 조회 결과",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "size": 0,
                                            "content": [
                                                {
                                                    "id": "79d52d77-6123-40f1-9f70-64bcbd6ca21a",
                                                    "boardType": "NOTICE",
                                                    "noticeType": "세션",
                                                    "title": "1차 정규세션 안내",
                                                    "content": "###1차 정규세션는 필수참여입니다.",
                                                    "writer": {
                                                        "name": "홍길동",
                                                        "generation": 25
                                                    }
                                                    "createdAt": "2024-09-09T00:00:00"
                                                 },
                                                 {
                                                    "id": "79d52d77-6123-40f1-9f70-64bcbd6ca21b",
                                                    "boardType": "NOTICE",
                                                    "noticeType": "세션",
                                                    "title": "2차 정규세션 안내",
                                                    "content": "##2차 정규세션는 필수참여입니다.",
                                                    "writer": {
                                                        "name": "홍길동",
                                                        "generation": 25
                                                    }
                                                    "createdAt": "2024-09-09T00:00:01"
                                                 },
                                                 {
                                                    "id": "79d52d77-6123-40f1-9f70-64bcbd6ca21c",
                                                    "boardType": "NOTICE",
                                                    "noticeType": "운영",
                                                    "title": "디스코드 운영 안내",
                                                    "content": "#디스코드 코드는 FG345GAD 입니다",
                                                    "writer": {
                                                        "name": "홍길동",
                                                        "generation": 25
                                                    }
                                                    "createdAt": "2024-09-09T00:00:02"
                                                 }
                                            ],
                                             "pageable": {
                                                "sort": {
                                                  "sorted": false,
                                                  "unsorted": true,
                                                  "empty": true
                                                },
                                                "offset": 0,
                                                "pageNumber": 0,
                                                "pageSize": 1,
                                                "paged": true,
                                                "unpaged": false
                                              },
                                              "totalPages": 1,
                                              "totalElements": 3,
                                              "last": false,
                                              "size": 1,
                                              "number": 0,
                                              "sort": {
                                                "sorted": false,
                                                "unsorted": true,
                                                "empty": true
                                              },
                                              "first": true,
                                              "numberOfElements": 3,
                                              "empty": false
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/boards")
    fun getBoards(
        @RequestParam(value = "lastCreatedAt", required = false) lastCreatedAt: String?,
        @RequestParam(value = "limit", defaultValue = "10") limit: Int,
        @RequestParam(value = "noticeType", required = false) noticeType: String?
    ): ResponseEntity<SuccessResponse<Page<BoardResponse>>>

    @Operation(
        summary = "공지사항 상세 조회",
        parameters = [
            Parameter(
                `in` = ParameterIn.PATH,
                name = "boardId",
                description = "공지사항 ID",
                required = true,
                schema = Schema(type = "long")
            )
        ]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "공지사항 상세 조회 결과",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "id": "79d52d77-6123-40f1-9f70-64bcbd6ca21a",
                                            "boardType": "NOTICE",
                                            "noticeType": "세션",
                                            "title": "1차 정규세션 안내",
                                            "content": "1차 정규세션는 필수참여입니다.",
                                            "writer": {
                                                "name": "홍길동",
                                                "generation": 25
                                            }
                                            "createdAt": "2024-09-09T00:00:00" 
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/boards/{boardId}")
    fun getBoardDetail(
        @PathVariable("boardId") boardId: String
    ): ResponseEntity<SuccessResponse<BoardResponse>>
}

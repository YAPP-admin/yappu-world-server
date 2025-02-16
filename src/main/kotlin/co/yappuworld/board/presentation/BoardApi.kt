package co.yappuworld.board.presentation

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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "공지사항 API", description = "공지사항 기능")
interface BoardApi {

    @Operation(
        summary = "공지사항 리스트 조회",
        parameters = [
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "page",
                required = true,
                schema = Schema(type = "long")
            ),
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "size",
                required = true,
                schema = Schema(type = "long")
            ),
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "displayTarget",
                required = false,
                schema = Schema(type = "string")
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
                                                    "boardType": "세션",
                                                    "title": "1차 정규세션 안내",
                                                    "content": "1차 정규세션는 필수참여입니다.",
                                                    "displayTarget": "활동회원,
                                                    "writer": "홍길동 10기",
                                                    "createdAt": "2024-09-09T00:00:00"
                                                 },
                                                 {
                                                    "id": "79d52d77-6123-40f1-9f70-64bcbd6ca21b",
                                                    "boardType": "세션",
                                                    "title": "2차 정규세션 안내",
                                                    "content": "2차 정규세션는 필수참여입니다.",
                                                    "displayTarget": "활동회원",
                                                    "writer": "홍길동 10기",
                                                    "createdAt": "2024-09-09T00:00:00"
                                                 },
                                                 {
                                                    "id": "79d52d77-6123-40f1-9f70-64bcbd6ca21c",
                                                    "boardType": "운영",
                                                    "title": "디스코드 운영 안내",
                                                    "content": "디스코드 코드는 FG345GAD 입니다",
                                                    "displayTarget": "정회원",
                                                    "writer": "홍길동 10기",
                                                    "createdAt": "2024-09-09T00:00:00"
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
                                                "pageSize": 10,
                                                "paged": true,
                                                "unpaged": false
                                              },
                                              "totalPages": 7,
                                              "totalElements": 20,
                                              "last": false,
                                              "size": 10,
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
    fun getBoards()

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
                                            "boardType": "세션",
                                            "title": "1차 정규세션 안내",
                                            "content": "1차 정규세션는 필수참여입니다.",
                                            "displayTarget": "활동회원,
                                            "writer": "홍길동 10기",
                                            "totalMembers": 100,
                                            "readCount": 38,
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
    )
}

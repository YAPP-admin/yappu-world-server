package co.yappuworld.global.config

import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse

object SwaggerCommonResponses {
    val values = listOf(
        SwaggerCommonResponse(
            "500",
            ApiResponse().apply {
                description = "Internal Server Error"
                content = Content().apply {
                    addMediaType(
                        "application/json",
                        MediaType().apply {
                            examples = mapOf(
                                "예기치 못한 에러" to Example().apply {
                                    value = """
                                        {
                                            "isSuccess": "false",
                                            "message": "예기치 못한 에러가 발생했습니다. 서버 관리자에게 문의해주세요.",
                                            "errorCode": "COM_0001"
                                        }
                                    """.trimIndent()
                                }
                            )
                        }
                    )
                }
            }
        ),
        SwaggerCommonResponse(
            "400",
            ApiResponse().apply {
                description = "Bad Request"
                content = Content().apply {
                    addMediaType(
                        "application/json",
                        MediaType().apply {
                            examples = mapOf(
                                "잘못된 파라미터 요청" to Example().apply {
                                    value = """
                                        {
                                            "isSuccess": "false",
                                            "message": "요청된 인자에 잘못된 값이 있습니다.",
                                            "errorCode": "COM_0002"
                                        }
                                    """.trimIndent()
                                }
                            )
                        }
                    )
                }
            }
        ),
        SwaggerCommonResponse(
            "401",
            ApiResponse().apply {
                description = "Unauthorized"
                content = Content().apply {
                    addMediaType(
                        "application/json",
                        MediaType().apply {
                            examples = mapOf(
                                "토큰 만료" to Example().apply {
                                    value = """
                                        {
                                            "isSuccess": "false",
                                            "message": "만료된 토큰입니다.",
                                            "errorCode": "TKN_0001"
                                        }
                                    """.trimIndent()
                                },
                                "비정상 토큰" to Example().apply {
                                    value = """
                                        {
                                            "isSuccess": "false",
                                            "message": "비정상 토큰입니다.",
                                            "errorCode": "TKN_0002"
                                        }
                                    """.trimIndent()
                                }
                            )
                        }
                    )
                }
            }
        )
    )
}

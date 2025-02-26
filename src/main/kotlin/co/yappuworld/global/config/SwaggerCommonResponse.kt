package co.yappuworld.global.config

import io.swagger.v3.oas.models.responses.ApiResponse

data class SwaggerCommonResponse(
    val code: String,
    val apiResponse: ApiResponse
)

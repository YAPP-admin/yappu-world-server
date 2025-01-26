package co.yappuworld.operation.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.presentation.dto.response.PositionsApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "운영 데이터 API")
interface OperationDataApi {

    @Operation(summary = "직군 정보")
    @GetMapping("/v1/positions")
    fun getPositions(): ResponseEntity<SuccessResponse<PositionsApiResponseDto>>
}

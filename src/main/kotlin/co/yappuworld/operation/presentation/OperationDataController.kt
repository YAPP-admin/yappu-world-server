package co.yappuworld.operation.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.presentation.dto.response.PositionApiResponseDto
import co.yappuworld.operation.presentation.dto.response.PositionsApiResponseDto
import co.yappuworld.user.domain.vo.Position
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class OperationDataController : OperationDataApi {

    override fun getPositions(): ResponseEntity<SuccessResponse<PositionsApiResponseDto>> {
        return ResponseEntity.ok(
            SuccessResponse.of(
                PositionsApiResponseDto(
                    Position.entries.map { PositionApiResponseDto.of(it) }
                )
            )
        )
    }
}

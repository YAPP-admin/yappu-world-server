package co.yappuworld.operation.application

import co.yappuworld.operation.application.dto.request.AdminGenerationPageAppRequestDto
import co.yappuworld.operation.application.dto.response.AdminGenerationsAppResponseDto
import co.yappuworld.operation.infrastructure.GenerationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminUserOperationService(
    private val generationRepository: GenerationRepository
) {

    @Transactional(readOnly = true)
    fun getGenerations(request: AdminGenerationPageAppRequestDto): AdminGenerationsAppResponseDto {
        val pageResponse = generationRepository.findAll(request.toPageRequest())
        return AdminGenerationsAppResponseDto(pageResponse)
    }
}

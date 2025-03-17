package co.yappuworld.operation.application

import co.yappuworld.operation.application.dto.response.AdminForceUpdateInfoAppResponseDto
import co.yappuworld.operation.domain.ConfigCategory
import co.yappuworld.operation.infrastructure.ConfigRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminOperationService(
    private val configRepository: ConfigRepository
) {

    @Transactional(readOnly = true)
    fun getForceUpdateInfos(): AdminForceUpdateInfoAppResponseDto =
        AdminForceUpdateInfoAppResponseDto.from(
            configRepository
                .findByCategory(ConfigCategory.FORCE_UPDATE)
                .associateBy { it.id }
        )
}

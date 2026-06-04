package co.yappuworld.external.storage

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.exception.GlobalError
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
@ConditionalOnProperty(
    prefix = "object-storage",
    name = ["enabled"],
    havingValue = "false"
)
class DisabledObjectStorageService : ObjectStorageService {
    override fun upload(
        file: MultipartFile,
        objectKey: String
    ): String = throw BusinessException(GlobalError.INTERNAL_SERVER_ERROR)

    override fun delete(objectKey: String) = Unit

    override fun getPublicUrl(objectKey: String): String = objectKey
}

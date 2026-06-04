package co.yappuworld.external.storage

import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest
import com.oracle.bmc.objectstorage.requests.PutObjectRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
@ConditionalOnProperty(prefix = "object-storage", name = ["enabled"], havingValue = "true")
class OciObjectStorageService(
    private val objectStorage: ObjectStorage,
    private val properties: OciObjectStorageProperties
) : ObjectStorageService {

    override fun upload(
        file: MultipartFile,
        objectKey: String
    ): String {
        file.inputStream.use { inputStream ->
            objectStorage.putObject(
                PutObjectRequest
                    .builder()
                    .namespaceName(properties.namespace)
                    .bucketName(properties.bucketName)
                    .objectName(objectKey)
                    .putObjectBody(inputStream)
                    .contentLength(file.size)
                    .contentType(file.contentType)
                    .build()
            )
        }

        return objectKey
    }

    override fun delete(objectKey: String) {
        objectStorage.deleteObject(
            DeleteObjectRequest
                .builder()
                .namespaceName(properties.namespace)
                .bucketName(properties.bucketName)
                .objectName(objectKey)
                .build()
        )
    }

    override fun getPublicUrl(objectKey: String): String = "${properties.publicBaseUrl.trimEnd('/')}/$objectKey"
}

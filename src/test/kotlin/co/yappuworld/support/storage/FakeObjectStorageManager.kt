package co.yappuworld.support.storage

import co.yappuworld.external.storage.ObjectStorageManager
import org.springframework.web.multipart.MultipartFile

class FakeObjectStorageManager : ObjectStorageManager {
    val uploadedObjectKeys = mutableListOf<String>()
    val deletedObjectKeys = mutableListOf<String>()

    override fun upload(
        file: MultipartFile,
        objectKey: String
    ): String {
        uploadedObjectKeys += objectKey
        return objectKey
    }

    override fun delete(objectKey: String) {
        deletedObjectKeys += objectKey
    }

    override fun getPublicUrl(objectKey: String): String = "https://image.yapp.co.kr/$objectKey"
}

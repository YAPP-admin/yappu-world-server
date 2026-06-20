package co.yappuworld.external.storage

import org.springframework.web.multipart.MultipartFile

interface ObjectStorageManager {
    fun upload(
        file: MultipartFile,
        objectKey: String
    ): String

    fun delete(objectKey: String)

    fun getPublicUrl(objectKey: String): String
}

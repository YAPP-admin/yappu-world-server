package co.yappuworld.external.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "object-storage")
data class OciObjectStorageProperties(
    val enabled: Boolean = false,
    val region: String,
    val namespace: String,
    val bucketName: String,
    val publicBaseUrl: String
)

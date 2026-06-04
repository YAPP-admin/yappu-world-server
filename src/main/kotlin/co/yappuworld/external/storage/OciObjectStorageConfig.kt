package co.yappuworld.external.storage

import com.oracle.bmc.Region
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.ObjectStorageClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    prefix = "object-storage",
    name = ["enabled"],
    havingValue = "true"
)
@EnableConfigurationProperties(OciObjectStorageProperties::class)
class OciObjectStorageConfig(
    private val properties: OciObjectStorageProperties
) {

    @Bean
    fun objectStorage(): ObjectStorage =
        ObjectStorageClient
            .builder()
            .region(Region.fromRegionId(properties.region))
            .build(
                InstancePrincipalsAuthenticationDetailsProvider
                    .builder()
                    .build()
            )
}

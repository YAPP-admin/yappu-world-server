package co.yappuworld.global.config

import co.yappuworld.global.property.FcmProperty
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FcmConfig(
    private val fcmProperty: FcmProperty
) {

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        val apps = FirebaseApp.getApps()
        return when (apps.isEmpty()) {
            true -> initializeApp()
            false -> getApp(apps)
        }.let { FirebaseMessaging.getInstance(it) }
    }

    private fun getApp(apps: List<FirebaseApp>): FirebaseApp {
        return try {
            apps.single { it.name == FirebaseApp.DEFAULT_APP_NAME }
        } catch (e: NoSuchElementException) {
            initializeApp()
        }
    }

    private fun initializeApp(): FirebaseApp {
        return FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(fcmProperty.toInputStream()))
                .build()
        )
    }
}

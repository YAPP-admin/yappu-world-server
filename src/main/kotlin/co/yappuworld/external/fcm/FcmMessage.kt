package co.yappuworld.external.fcm

data class FcmMessage(
    val title: String,
    val body: String,
    val deeplink: String
) {
    fun getData(): Map<String, String> =
        mapOf(
            "title" to title,
            "body" to body,
            "deeplink" to deeplink
        )
}

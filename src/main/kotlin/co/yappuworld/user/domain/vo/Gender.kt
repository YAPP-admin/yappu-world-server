package co.yappuworld.user.domain.vo

enum class Gender(
    val label: String
) {
    MAN("남"),
    WOMAN("여");

    companion object {
        fun fromLabel(label: String) = entries.find { it.label == label }
    }
}

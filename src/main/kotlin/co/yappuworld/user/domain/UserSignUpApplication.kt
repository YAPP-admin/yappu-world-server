package co.yappuworld.user.domain

data class UserSignUpApplication(
    val email: String,
    val password: String,
    val name: String,
    val activityUnits: List<ActivityUnit>
)

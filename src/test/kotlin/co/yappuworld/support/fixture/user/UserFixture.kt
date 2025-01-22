package co.yappuworld.support.fixture.user

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.model.SignUpApplicantDetails
import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.model.User
import co.yappuworld.user.domain.vo.UserRole

fun getUserFixture(
    email: String = "email@email.com",
    password: String = "password",
    name: String = "name",
    role: UserRole = UserRole.ACTIVE,
    isActive: Boolean = true
) = User(
    email = email,
    password = password,
    name = name,
    role = role,
    isActive = isActive
)

fun getSignUpApplication(details: SignUpApplicantDetails = getSignUpApplicationDetails()): SignUpApplication {
    return SignUpApplication(details)
}

fun getSignUpApplicationDetails(
    email: String = "email@email.com",
    password: String = "abcabC!!",
    name: String = "name",
    activityUnits: List<ActivityUnit> = getActivityUnits()
): SignUpApplicantDetails {
    return SignUpApplicantDetails(
        email,
        password,
        name,
        activityUnits
    )
}

fun getActivityUnits(
    vararg activityUnits: ActivityUnit = arrayOf(
        ActivityUnit(1, Position.PM)
    )
): List<ActivityUnit> {
    return activityUnits.toList()
}

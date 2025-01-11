package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.UserSignUpApplicationStatus
import co.yappuworld.user.domain.UserSignUpApplications
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserSignUpApplicationRepository : CrudRepository<UserSignUpApplications, UUID> {

    fun findByApplicantEmailAndStatusIn(
        applicantEmail: String,
        status: List<UserSignUpApplicationStatus>
    ): List<UserSignUpApplications>
}
package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import org.springframework.data.domain.Limit
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserSignUpApplicationRepository :
    CrudRepository<SignUpApplication, UUID>,
    PagingAndSortingRepository<SignUpApplication, UUID> {

    fun findByApplicantEmailAndStatus(
        applicantEmail: String,
        status: SignUpApplicationStatus
    ): List<SignUpApplication>

    fun findByApplicantEmailOrderByUpdatedAtDesc(
        applicantEmail: String,
        limit: Limit
    ): SignUpApplication?

    fun findAllByIdIn(ids: List<UUID>): List<SignUpApplication>
}

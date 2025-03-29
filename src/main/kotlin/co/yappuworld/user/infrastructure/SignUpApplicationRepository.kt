package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.SignUpApplicationEntity
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import org.springframework.data.domain.Limit
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SignUpApplicationRepository : JpaRepository<SignUpApplicationEntity, UUID> {

    fun findByApplicantEmailAndStatus(
        applicantEmail: String,
        status: SignUpApplicationStatus
    ): List<SignUpApplicationEntity>

    fun findByApplicantEmailOrderByUpdatedAtDesc(
        applicantEmail: String,
        limit: Limit
    ): SignUpApplicationEntity?

    fun findAllByIdIn(ids: List<UUID>): List<SignUpApplicationEntity>
}

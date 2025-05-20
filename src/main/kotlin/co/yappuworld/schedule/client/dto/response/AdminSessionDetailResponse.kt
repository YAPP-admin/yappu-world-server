package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.SessionParticipant
import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.user.domain.vo.Position
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AdminSessionDetailResponse(
    @Schema(description = "세션 ID")
    val id: UUID,
    @Schema(description = "세션 이름")
    val name: String,
    @Schema(description = "기수")
    val generation: Int,
    @Schema(description = "세션 장소")
    val place: String?,
    @Schema(description = "세션 시작 일자")
    val date: LocalDate,
    @Schema(description = "세션 종료 일자")
    val endDate: LocalDate,
    @Schema(description = "세션 시작 시간")
    val time: LocalTime,
    @Schema(description = "세션 종료 시간")
    val endTime: LocalTime,
    @Schema(description = "세션 타입")
    val sessionType: SessionType,
    @Schema(description = "세션 참여자 목록")
    val participantsByPosition: List<AdminSessionParticipantsByPositionResponse>
) {

    companion object {
        fun from(
            session: SessionEntity,
            participants: List<SessionParticipant>
        ): AdminSessionDetailResponse {
            val participantsGroupedByPosition = participants.groupBy { it.position }
            val participantsByPosition =
                Position.participantPositions.map { position ->
                    participantsGroupedByPosition[position]?.let { positionParticipants ->
                        AdminSessionParticipantsByPositionResponse.from(
                            position = position.label,
                            participants = positionParticipants
                        )
                    } ?: AdminSessionParticipantsByPositionResponse.from(
                        position = position.label,
                        participants = emptyList()
                    )
                }

            return AdminSessionDetailResponse(
                id = session.id,
                name = session.name,
                generation = session.generation,
                place = session.place,
                date = session.date,
                endDate = session.endDate,
                time = session.time,
                endTime = session.endTime,
                sessionType = session.sessionType,
                participantsByPosition = participantsByPosition
            )
        }
    }
}

data class AdminSessionParticipantsByPositionResponse(
    @Schema(description = "유저 직군")
    val position: String,
    @Schema(description = "유저 목록")
    val participants: List<AdminSessionParticipantResponse>
) {

    companion object {

        fun from(
            position: String,
            participants: List<SessionParticipant>
        ) = AdminSessionParticipantsByPositionResponse(
            position = position,
            participants = participants.map {
                AdminSessionParticipantResponse(
                    id = it.userId,
                    name = it.userName,
                    position = it.position.label
                )
            }
        )
    }
}

data class AdminSessionParticipantResponse(
    @Schema(description = "유저 ID")
    val id: UUID,
    @Schema(description = "유저 이름")
    val name: String,
    @Schema(description = "유저 직군")
    val position: String
)

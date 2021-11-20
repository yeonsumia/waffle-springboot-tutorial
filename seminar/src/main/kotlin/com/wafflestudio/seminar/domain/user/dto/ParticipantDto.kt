package com.wafflestudio.seminar.domain.user.dto

import com.wafflestudio.seminar.domain.seminar.model.Seminar
import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.seminar.model.SeminarParticipant
import java.time.LocalDateTime
class ParticipantDto {
    data class Response(
            val id: Long,
            val name: String,
            val email: String,
            val university: String?,
            val joinedAt: LocalDateTime?,
            val isActive: Boolean,
            val droppedAt: LocalDateTime?
    ) {
        constructor(user: User, seminarParticipant: SeminarParticipant) : this(
                id = user.id,
                name = user.name,
                email = user.email,
                university = user.participantProfile?.university,
                joinedAt = seminarParticipant.joinedAt,
                isActive = seminarParticipant.isActive,
                droppedAt = seminarParticipant.droppedAt
        )
    }

    data class ParticipantRequest(
        val university: String?,
        val accepted: String?
    )


}
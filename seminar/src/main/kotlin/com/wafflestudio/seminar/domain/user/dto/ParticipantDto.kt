package com.wafflestudio.seminar.domain.user.dto

import com.wafflestudio.seminar.domain.seminar.model.Seminar
import com.wafflestudio.seminar.domain.user.model.User

class ParticipantDto {
    data class Response(
            val id: Long,
            val name: String,
            val email: String,
            val university: String?
    ) {
        constructor(user: User) : this(
                id = user.id,
                name = user.name,
                email = user.email,
                university = user.participantProfile?.university,
        )
    }

    data class ParticipantRequest(
        val university: String?,
        val accepted: String?
    )


}
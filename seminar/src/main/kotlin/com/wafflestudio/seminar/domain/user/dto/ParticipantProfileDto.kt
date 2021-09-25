package com.wafflestudio.seminar.domain.user.dto

import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.user.model.ParticipantProfile
import com.wafflestudio.seminar.domain.user.model.InstructorProfile
import com.wafflestudio.seminar.domain.seminar.dto.SeminarsDto
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

class ParticipantProfileDto {
    data class Response(
        val id: Long?,
        val university: String?,
        val accepted: Boolean?,
        val seminars: List<SeminarsDto.Response>? = listOf()
    ) {
        constructor(participantProfile: ParticipantProfile): this(
            id = participantProfile.id,
            university = participantProfile.university,
            accepted = participantProfile.accepted,
            seminars = participantProfile.seminars.map { it -> SeminarsDto.Response(it) }
        )
    }
}
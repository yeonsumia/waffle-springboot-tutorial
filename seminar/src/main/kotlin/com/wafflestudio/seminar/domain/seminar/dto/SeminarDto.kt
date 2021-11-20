package com.wafflestudio.seminar.domain.seminar.dto

import com.wafflestudio.seminar.domain.seminar.model.Seminar
import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.user.model.ParticipantProfile
import com.wafflestudio.seminar.domain.user.model.InstructorProfile
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import com.fasterxml.jackson.annotation.JsonFormat
import com.wafflestudio.seminar.domain.user.dto.InstructorDto
import com.wafflestudio.seminar.domain.user.dto.ParticipantDto
import com.wafflestudio.seminar.domain.user.repository.UserRepository
import com.wafflestudio.seminar.domain.seminar.repository.SeminarParticipantRepository
import com.wafflestudio.seminar.domain.user.dto.InstructorProfileDto

class SeminarDto {
    data class Response(
            val id: Long,
            val name: String,
            val capacity: Long,
            val count: Long,
            val time: String,
            val online: Boolean,
            val instructors: List<InstructorDto.Response>,
            val participants: List<ParticipantDto.Response>

    ) {
        constructor(seminar: Seminar) : this(
                id = seminar.id,
                name = seminar.name,
                capacity = seminar.capacity,
                count = seminar.count,
                time = seminar.time,
                online = seminar.online,
                instructors = seminar.instructors.map { InstructorDto.Response(it.user!!) },
                participants = seminar.participants.map { ParticipantDto.Response(it.participantProfile.user!!, it.participantProfile.findSeminarParticipantBySeminar(seminar)) }
        )
    }

    data class SeminarRequest(
            val name: String,

            val capacity: Long,

            val count: Long,

            val time: String,

            val online: String?,

            )

    data class JoinSeminarRequest(
            @field:NotBlank
            val role: String,

            )
}
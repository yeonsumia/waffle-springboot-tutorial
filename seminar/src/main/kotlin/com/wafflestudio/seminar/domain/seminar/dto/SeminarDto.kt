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
        constructor(seminar: Seminar, userRepository: UserRepository) : this(
                id = seminar.id,
                name = seminar.name,
                capacity = seminar.capacity,
                count = seminar.count,
                time = seminar.time,
                online = seminar.online,
                instructors = seminar.instructors.map { it -> InstructorDto.Response(userRepository.findUserByEmail(it.user!!.email)) },
                participants = seminar.participants.map { it -> ParticipantDto.Response(userRepository.findUserByEmail(it.participantProfile.user!!.email), it.participantProfile.findSeminarParticipantBySeminar(seminar)) }
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
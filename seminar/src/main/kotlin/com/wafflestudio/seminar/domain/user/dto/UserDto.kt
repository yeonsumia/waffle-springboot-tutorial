package com.wafflestudio.seminar.domain.user.dto

import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.user.dto.InstructorProfileDto
import com.wafflestudio.seminar.domain.user.dto.ParticipantProfileDto
import net.bytebuddy.implementation.bind.annotation.Empty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class UserDto {
    data class Response(
        val id: Long,
        val email: String,
        val name: String,
        val roles: String,
        val participantProfile: ParticipantProfileDto.Response? = null,
        val instructorProfile: InstructorProfileDto.Response? = null
    ) {
        constructor(user: User) : this(
            id = user.id,
            email = user.email,
            name = user.name,
            roles = user.roles,
            participantProfile = user.participantProfile?.let { it -> ParticipantProfileDto.Response(it) },
            instructorProfile = user.instructorProfile?.let { it -> InstructorProfileDto.Response(it) }
        )
    }

    data class SignupRequest(

        @NotBlank
        val email: String?,

        @NotBlank
        val name: String?,

        @NotBlank
        val password: String?,

        @NotBlank
        val role: String?,

        val university: String?,

        val accepted: String?,

        val year: Long?,

        val company: String?,

    )

    data class PutRequest(

        val email: String?,

        val name: String?,

        val password: String?,

        val university: String?,

        val accepted: String?,

        val year: Long?,

        val company: String?,

        )
}
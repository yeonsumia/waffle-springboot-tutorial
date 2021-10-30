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
class ShowSeminarDto {
    data class Response(
        val id: Long,
        val name: String,
        val instructors: List<InstructorDto.Response>,
        val participantCount: Int,

    ) {
        constructor(seminar: Seminar, userRepository: UserRepository) : this(
            id = seminar.id,
            name = seminar.name,
            instructors = seminar.instructors.map { InstructorDto.Response(userRepository.findUserByEmail(it.user!!.email)) },
            participantCount = seminar.participants.filter { it.isActive }.size
        )
    }

}
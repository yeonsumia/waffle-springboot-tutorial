package com.wafflestudio.seminar.domain.user.dto

import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.seminar.dto.ChargeDto
import com.wafflestudio.seminar.domain.user.model.InstructorProfile
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

class InstructorProfileDto {
    data class Response(
            val id: Long?,
            val company: String?,
            val year: Long?,
            val charge: ChargeDto.Response?,
    ) {
        constructor(instructorProfile: InstructorProfile): this(
                id = instructorProfile.id,
                company = instructorProfile.company,
                year = instructorProfile.year,
                charge = instructorProfile.seminar?.let { it -> ChargeDto.Response(it) }
        )
    }
}
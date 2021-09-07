package com.wafflestudio.seminar.domain.survey.dto

import com.fasterxml.jackson.annotation.JsonProperty

import com.wafflestudio.seminar.domain.os.dto.OperatingSystemDto
import com.wafflestudio.seminar.domain.os.model.OperatingSystem

import com.wafflestudio.seminar.domain.user.model.UserResponse

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Max
import javax.validation.constraints.Min

class SurveyResponseDto {
    data class Response(
        var id: Long? = 0,
        var user: UserResponse? = null,
        var os: OperatingSystem? = null,
        var springExp: Int = 0,
        var rdbExp: Int = 0,
        var programmingExp: Int = 0,
        var major: String? = "",
        var grade: String? = "",
        var backendReason: String? = "",
        var waffleReason: String? = "",
        var somethingToSay: String? = "",
        var timestamp: LocalDateTime? = null
    )

    // TODO: 아래 두 DTO 완성
    data class CreateRequest(
        var user: UserResponse? = null,

        @field:NotBlank
        var os: String = "",

        @field:Min(1, message = "The value must be between 1 and 5")
        @field:Max(5, message = "The value must be between 1 and 5")
        var spring_exp: Int = 0,

        @field:Min(1, message = "The value must be between 1 and 5")
        @field:Max(5, message = "The value must be between 1 and 5")
        var rdb_exp: Int = 0,

        @field:Min(1, message = "The value must be between 1 and 5")
        @field:Max(5, message = "The value must be between 1 and 5")
        var programming_exp: Int = 0,

        var major: String? = null,
        var grade: String? = null,
        var backend_reason: String? = null,
        var waffle_reason: String? = null,
        var something_to_say: String? = null
    )

    data class ModifyRequest(
        var something: String? = ""
        // 예시 - 지우고 새로 생성
    )
}

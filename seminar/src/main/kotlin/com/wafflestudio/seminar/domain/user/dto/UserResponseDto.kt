package com.wafflestudio.seminar.domain.user.dto

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

class UserResponseDto {
    data class Response(
        var id: Long? = null,
        var name: String = "",
        var email: String = "",
    )

    data class CreateRequest(

        @field:NotBlank
        var name: String = "",

        // unique constraint with JPA and Bean Validation 은 없음.
        @field:NotBlank
        var email: String = "",

    )

}

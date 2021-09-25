package com.wafflestudio.seminar.domain.seminar.dto

import com.wafflestudio.seminar.domain.seminar.model.Seminar

class ChargeDto {
    data class Response(
            val id: Long,
            val name: String,
    ) {
        constructor(seminar: Seminar) : this(
                id = seminar.id,
                name = seminar.name
        )
    }

}
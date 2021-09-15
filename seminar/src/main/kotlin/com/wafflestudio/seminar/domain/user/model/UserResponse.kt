package com.wafflestudio.seminar.domain.user.model

import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

@Entity
class UserResponse(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @NotEmpty
    @Column
    var name: String? = null,

    @NotEmpty
    @Column(unique=true)
    var email: String? = null,
)

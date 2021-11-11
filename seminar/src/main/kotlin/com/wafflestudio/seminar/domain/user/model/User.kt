package com.wafflestudio.seminar.domain.user.model

import com.wafflestudio.seminar.domain.model.BaseEntity
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

import com.wafflestudio.seminar.domain.user.model.ParticipantProfile
import com.wafflestudio.seminar.domain.user.model.InstructorProfile

@Entity
@Table(name = "seminar_user")
class User(
    @Column(unique = true)
    @field:NotBlank
    var email: String,

    @field:NotBlank
    var name: String,

    @field:NotBlank
    var password: String,

    @Column
    @field:NotNull
    var roles: String = "",

    @OneToOne(cascade=[CascadeType.MERGE])
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    var participantProfile: ParticipantProfile? = null,

    @OneToOne(cascade=[CascadeType.MERGE])
    @JoinColumn(name="instructor_id", referencedColumnName = "id")
    var instructorProfile: InstructorProfile? = null,


    ) : BaseEntity()
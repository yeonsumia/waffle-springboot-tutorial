package com.wafflestudio.seminar.domain.user.model

import com.wafflestudio.seminar.domain.model.BaseEntity
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "seminar_user")
class User(
    @Column(unique = true)
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val password: String,

    @Column
    @field:NotNull
    val role: String = "",

    @OneToOne
    @JoinColumn(name="participant_id" referencedColumnName = "id")
    @field:NotNull
    val participantProfile: ParticipantProfile,

    @OneToOne
    @JoinColumn(name="instructor_id" referencedColumnName = "id")
    @field:NotNull
    val instructorProfile: InstructorProfile,

    ) : BaseEntity()


@Entity
class ParticipantProfile (

    @field:NotNull
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @field:NotNull
    val updatedAt: LocalDateTime = LocalDateTime.now(),

) : BaseEntity()

@Entity
class InstructorProfile (

    @field:NotNull
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @field:NotNull
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    ) : BaseEntity()
package com.wafflestudio.seminar.domain.user.model

import com.wafflestudio.seminar.domain.model.BaseTimeEntity
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import com.wafflestudio.seminar.domain.seminar.model.SeminarParticipant
import com.wafflestudio.seminar.domain.seminar.model.Seminar
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.FetchType.*

@Entity
class ParticipantProfile (
    @Column
    var university: String = "",

    @Column
    var accepted: Boolean = true,

    @OneToMany(mappedBy="participantProfile", fetch=FetchType.EAGER, cascade=[CascadeType.ALL])
    val seminars: MutableList<SeminarParticipant> = mutableListOf(),

    @JsonIgnore
    @OneToOne(mappedBy = "participantProfile")
    val user: User? = null,

    ) : BaseTimeEntity() {
        fun joinSeminar(seminarParticipant: SeminarParticipant) {
            seminars.add(seminarParticipant)
        }
    }
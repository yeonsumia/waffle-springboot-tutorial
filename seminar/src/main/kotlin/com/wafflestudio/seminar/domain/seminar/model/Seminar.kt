package com.wafflestudio.seminar.domain.seminar.model

import com.wafflestudio.seminar.domain.model.BaseTimeEntity
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.persistence.*
import com.wafflestudio.seminar.domain.seminar.model.SeminarParticipant
import com.wafflestudio.seminar.domain.user.model.InstructorProfile
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.FetchType.*

@Entity
class Seminar(

    @field:NotBlank
    var name: String,

    var capacity: Long,

    var count: Long,

    @field:NotBlank
    var time: String,

    var online: Boolean = true,

    @JsonIgnore
    @OneToMany(mappedBy="seminar", fetch=FetchType.LAZY, cascade=[CascadeType.ALL], orphanRemoval = true)
    var instructors: MutableList<InstructorProfile> = mutableListOf(),

    @JsonIgnore
    @OneToMany(mappedBy="seminar", fetch=FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var participants: MutableList<SeminarParticipant> = mutableListOf(),

    @JsonIgnore
    @OneToMany(mappedBy="seminar", fetch=FetchType.LAZY, cascade=[CascadeType.ALL], orphanRemoval = true)
    var mainInstructors: MutableList<InstructorProfile> = mutableListOf(),


) : BaseTimeEntity() {
    public fun addInstructor(instructorProfile: InstructorProfile) {
        instructors.add(instructorProfile)
    }

    public fun addMainInstructor(instructorProfile: InstructorProfile) {
        mainInstructors.add(instructorProfile)
    }

    public fun addParticipant(seminarParticipant: SeminarParticipant) {
        participants.add(seminarParticipant)
    }

}
package com.wafflestudio.seminar.domain.user.model

import com.wafflestudio.seminar.domain.model.BaseTimeEntity
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.seminar.model.Seminar
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty


@Entity
class InstructorProfile (
    @Column
    var company: String = "",

    @Column
    var year: Long? = null,

    @ManyToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name="seminar_id", referencedColumnName = "id")
    var seminar: Seminar? = null,

    @JsonIgnore
    @OneToOne(mappedBy = "instructorProfile", cascade = [CascadeType.MERGE])
    var user: User? = null,

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="main_seminar_id", referencedColumnName = "id")
    var mainSeminar: Seminar? = null,

    ) : BaseTimeEntity()
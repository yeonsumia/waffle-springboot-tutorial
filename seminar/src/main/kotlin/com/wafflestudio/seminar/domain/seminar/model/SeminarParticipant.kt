package com.wafflestudio.seminar.domain.seminar.model

import com.wafflestudio.seminar.domain.model.BaseEntity
import javax.persistence.*
import com.wafflestudio.seminar.domain.user.model.ParticipantProfile
import com.wafflestudio.seminar.domain.seminar.model.Seminar
import java.time.LocalDateTime

@Entity
class SeminarParticipant(

    @ManyToOne
    @JoinColumn(name = "seminar_id", referencedColumnName = "id")
    val seminar: Seminar,

    @ManyToOne
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    val participantProfile: ParticipantProfile,

    @Column
    val joinedAt: LocalDateTime? = null,

    @Column
    var isActive: Boolean = true,

    @Column
    var droppedAt: LocalDateTime? = null,

    ) : BaseEntity()
package com.wafflestudio.seminar.domain.seminar.repository

import com.wafflestudio.seminar.domain.seminar.model.SeminarParticipant
import org.springframework.data.jpa.repository.JpaRepository

interface SeminarParticipantRepository: JpaRepository<SeminarParticipant, Long?> {

}

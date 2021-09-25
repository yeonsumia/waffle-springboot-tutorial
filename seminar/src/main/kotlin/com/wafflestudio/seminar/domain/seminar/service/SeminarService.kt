package com.wafflestudio.seminar.domain.seminar.service

import org.springframework.stereotype.Service
import com.wafflestudio.seminar.domain.seminar.dto.SeminarDto
import com.wafflestudio.seminar.domain.seminar.dto.ShowSeminarDto
import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.seminar.model.Seminar
import com.wafflestudio.seminar.domain.seminar.model.SeminarParticipant
import com.wafflestudio.seminar.domain.seminar.exception.RequestIsNullException
import com.wafflestudio.seminar.domain.seminar.exception.RequestInvalidFormException
import com.wafflestudio.seminar.domain.seminar.exception.UserNotAllowedException
import com.wafflestudio.seminar.domain.seminar.exception.SeminarNotFoundException
import com.wafflestudio.seminar.domain.seminar.repository.SeminarRepository
import com.wafflestudio.seminar.domain.seminar.repository.SeminarParticipantRepository
import com.wafflestudio.seminar.domain.user.repository.UserRepository
import com.wafflestudio.seminar.domain.user.dto.InstructorDto
import com.wafflestudio.seminar.domain.seminar.dto.ChargeDto
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@Service
class SeminarService (
    private val seminarRepository: SeminarRepository,
    private val userRepository: UserRepository,
    private val seminarParticipantRepository: SeminarParticipantRepository
) {
    fun addSeminar(seminarRequest: SeminarDto.SeminarRequest, user: User) : Seminar {
        if (!user.roles.contains("instructor")) throw UserNotAllowedException("User is not a instructor.")
        val name = seminarRequest.name
        val capacity = seminarRequest.capacity
        val count = seminarRequest.count
        val time = seminarRequest.time
        val online = seminarRequest.online

        checkSeminarRequestForm(time, name, count, capacity, online)
        val booleanOnline = online.lowercase().toBoolean()

        val newSeminar = seminarRepository.save(Seminar(name, capacity, count, time, booleanOnline))
        user.instructorProfile?.seminar = newSeminar
        user.instructorProfile?.mainSeminar = newSeminar

        newSeminar.addInstructor(user.instructorProfile!!)
        newSeminar.addMainInstructor(user.instructorProfile!!)
        return seminarRepository.save(newSeminar)
    }

    fun modifySeminar(id: Long, seminarRequest: SeminarDto.SeminarRequest?, user: User) : Seminar {
        val seminar = seminarRepository.findSeminarById(id)
        if(seminar == null) throw SeminarNotFoundException("Seminar is not found.")
        if(!user.roles.contains("instructor") || seminar.mainInstructors.indexOf(user.instructorProfile) != -1) throw UserNotAllowedException("User is cannot modify seminar info.")
        if(seminarRequest == null) return seminar;

        val name = seminarRequest.name
        val capacity = seminarRequest.capacity
        val count = seminarRequest.count
        val time = seminarRequest.time
        val online = seminarRequest.online

        if(capacity < seminar.participants.filter { it.isActive }.size.toLong()) throw RequestInvalidFormException("Capacity should be bigger than the number of active users.")

        checkSeminarRequestForm(time, name, count, capacity, online)
        val booleanOnline = online.lowercase().toBoolean()

        seminar.name = name
        seminar.capacity = capacity
        seminar.count = count
        seminar.time = time
        seminar.online = booleanOnline

        return seminarRepository.findSeminarById(id)!!
    }

    private fun checkSeminarRequestForm(time: String, name: String, count: Long, capacity: Long, online: String) {
        if (!Regex("([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]").matches(time)) throw RequestInvalidFormException("Time Request includes invalid form data.")
        if (name.isEmpty()) throw RequestIsNullException("Request includes null(empty) value.")
        if (count <= 0L || capacity <= 0L) throw RequestInvalidFormException("Number Request includes invalid form data.")
        if (online.lowercase() != "true" && online.lowercase() != "false") throw RequestInvalidFormException("Online Request includes invalid form data.")
    }

    fun getSeminarById(id: Long): Seminar {
        return seminarRepository.findByIdOrNull(id) ?: throw SeminarNotFoundException("Seminar is not found.")
    }

    fun getSeminarsByName(name: String, order: Boolean): List<ShowSeminarDto.Response> {
        val seminars = seminarRepository.findAll()
        var seminarList = seminars.filter { it -> it.name.contains(name) }.sortedWith(compareBy({it.createdAt}))
        if(order) seminarList = seminarList.reversed()
        return seminarList.map { it -> ShowSeminarDto.Response(it, userRepository) }
    }

    fun getAllSeminars(order: Boolean): List<ShowSeminarDto.Response> {
        val seminars = seminarRepository.findAll()
        var seminarList = seminars.sortedWith(compareBy({it.createdAt}))
        if(order) seminarList = seminarList.reversed()
        return seminarList.map { it -> ShowSeminarDto.Response(it, userRepository) }
    }

    fun joinSeminar(id: Long, joinSeminarRequest: SeminarDto.JoinSeminarRequest, user: User): Seminar {
        val seminar = seminarRepository.findSeminarById(id)
        if(seminar == null) throw SeminarNotFoundException("Seminar is not found.")
        val role = joinSeminarRequest.role

        if(role == "participant") {
            if(!user.roles.contains("participant")) throw UserNotAllowedException("You do not have permission to join the seminar as a participant.")
            if(!user.participantProfile!!.accepted) throw UserNotAllowedException("You have not been accepted to join the seminar.")
            if(seminar.instructors.indexOf(user.instructorProfile) != -1) throw RequestInvalidFormException("You are already an instructor of the seminar.")
            if(seminar.participants.size.toLong() == seminar.capacity) throw RequestInvalidFormException("There is no vancancy in the seminar.")
            val seminarFiltered = seminar.participants.filter { it.participantProfile.user!!.email == user.email }
            if(seminarFiltered.size != 0 && !seminarFiltered[0].isActive) throw RequestInvalidFormException("You cannot join the seminar that you dropped again.")
            val seminarParticipant = SeminarParticipant(seminar, user.participantProfile!!, LocalDateTime.now())
            seminarParticipantRepository.save(seminarParticipant)
            user.participantProfile!!.joinSeminar(seminarParticipant)
            seminar.addParticipant(seminarParticipant)

        } else if(role == "instructor") {
            if(!user.roles.contains("instructor")) throw UserNotAllowedException("You do not have permission to join the seminar as an instructor.")
            if(seminar.instructors.indexOf(user.instructorProfile) != -1) throw RequestInvalidFormException("You are already an instructor of the seminar.")
            if(user.instructorProfile!!.mainSeminar != null) throw RequestInvalidFormException("You are already an main instructor of another seminar.")

            user.instructorProfile!!.seminar = seminar
            seminar.addInstructor(user.instructorProfile!!)

        } else throw RequestInvalidFormException("You should join the seminar as a participant or an instructor.")

        return seminarRepository.findSeminarById(id)!!
    }

    fun dropSeminar(id: Long, user: User) : Seminar {
        val seminar = seminarRepository.findSeminarById(id)
        if(seminar == null) throw SeminarNotFoundException("Seminar is not found.")
        if(seminar.instructors.indexOf(user.instructorProfile) != -1) throw UserNotAllowedException("Instructor cannot drop the seminar.")
//        user의 주소 값은 서로 다르다.
//        seminar.participants.map { it -> System.out.println(it.participantProfile.user!!.email) }
//        System.out.println(user.email)
        if(seminar.participants.filter { it.participantProfile.user!!.email == user.email }.size == 0) return seminar

        seminar.participants.find { it.seminar == seminar }!!.isActive = false
        seminar.participants.find { it.seminar == seminar }!!.droppedAt = LocalDateTime.now()

        return seminarRepository.findSeminarById(id)!!

    }


}
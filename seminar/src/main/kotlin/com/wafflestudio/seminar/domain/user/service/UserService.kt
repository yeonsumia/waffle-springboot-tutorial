package com.wafflestudio.seminar.domain.user.service

import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.user.repository.UserRepository
import com.wafflestudio.seminar.domain.user.dto.UserDto
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.wafflestudio.seminar.domain.user.repository.ParticipantRepository
import com.wafflestudio.seminar.domain.user.model.ParticipantProfile
import com.wafflestudio.seminar.domain.user.repository.InstructorRepository
import com.wafflestudio.seminar.domain.user.model.InstructorProfile
import com.wafflestudio.seminar.domain.user.dto.ParticipantDto
import com.wafflestudio.seminar.domain.user.exception.*
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val participantRepository: ParticipantRepository,
    private val instructorRepository: InstructorRepository,
    private var participant: ParticipantProfile?,
    private var instructor: InstructorProfile?
) {
    fun signup(signupRequest: UserDto.SignupRequest): User {
        val email = signupRequest.email
        val name = signupRequest.name
        val encodedPassword = passwordEncoder.encode(signupRequest.password)
        val roles = signupRequest.role
        if(email.isNullOrBlank() || name.isNullOrBlank() || signupRequest.password.isNullOrBlank() || roles.isNullOrBlank()) throw InvaildUserForm("You need to fill the name, email, password, roles for sign up.")
        if (userRepository.existsByEmail(email)) throw UserAlreadyExistsException()
        var company = signupRequest.company
        val year = signupRequest.year
        var university = signupRequest.university
        var accepted = signupRequest.accepted
        participant = null
        instructor = null
        var user = User(email, name, encodedPassword, roles, participant, instructor)
        if (roles.equals("instructor")) {
            if (year != null && !(year > 0)) throw YearTypeException("Year should be a number that bigger than 0.")
            if(company == null) company = ""
            instructor = instructorRepository.save(InstructorProfile(company, year))
            user.instructorProfile = instructor
            instructor!!.user = user
        }
        else if (roles.equals("participant")) {
            if(university == null) university = ""
            if(accepted == null) accepted = "true"
            val boolean_accepted = accepted.lowercase().toBoolean()
            participant = participantRepository.save(ParticipantProfile(university, boolean_accepted))
            user.participantProfile = participant
            participant!!.user = user
        }
        else if (roles.indexOf(',') != -1) {
            val rolesSet = roles.split(",").toSet()
            if(rolesSet == setOf<String>("instructor", "participant")) {
                if (year != null && !(year > 0)) throw YearTypeException("Year should be a number that bigger than 0.")
                if(company == null) company = ""
                if(university == null) university = ""
                if(accepted == null) accepted = "true"
                val boolean_accepted = accepted.lowercase().toBoolean()
                instructor = instructorRepository.save(InstructorProfile(company, year))
                user.instructorProfile = instructor
                instructor!!.user = user
                participant = participantRepository.save(ParticipantProfile(university, boolean_accepted))
                user.participantProfile = participant
                participant!!.user = user
            }
        } else throw RoleTypeException("Role should be participant or instructor.")

        return userRepository.save(user)
    }

    fun modifyUser(putRequest: UserDto.PutRequest, user: User): User {
        val year = putRequest.year
        var company = putRequest.company
        var university = putRequest.university
        var accepted = putRequest.accepted
        val password = putRequest.password
        val name = putRequest.name
        val email = putRequest.email
        if(year == null && company == null && university == null && accepted == null && password.isNullOrBlank() && name.isNullOrBlank() && email.isNullOrBlank()) return user

        if(email.isNullOrBlank() || name.isNullOrBlank() || password.isNullOrBlank()) throw InvaildUserForm("You need to fill the name, email, password, roles.")
        val encodedPassword = passwordEncoder.encode(putRequest.password)
        user.password = encodedPassword
        user.name = name
        user.email = email
        // roles는 바꿀 수 없는 character로 간주함.
        val roles = user.roles

        if (roles.equals("instructor")) {
            if (year != null && !(year > 0)) throw YearTypeException("Year should be a number that bigger than 0.")
            if(company == null) company = ""
            user.instructorProfile?.company = company
            user.instructorProfile?.year = year
            user.instructorProfile?.updatedAt = LocalDateTime.now()
        }
        else if (roles.equals("participant")) {
            if(university == null) university = ""
            if(accepted == null) accepted = "true"
            val boolean_accepted = accepted.lowercase().toBoolean()
            user.participantProfile?.university = university
            user.participantProfile?.accepted = boolean_accepted
            user.participantProfile?.updatedAt = LocalDateTime.now()
        }
        else if (roles.indexOf(',') != -1) {
            val rolesSet = roles.split(",").toSet()
            if(rolesSet == setOf<String>("instructor", "participant")) {
                if (year != null && !(year > 0)) throw YearTypeException("Year should be a number that bigger than 0.")
                if(university == null) university = ""
                if(company == null) company = ""
                if(accepted == null) accepted = "true"
                val boolean_accepted = accepted.lowercase().toBoolean()
                user.participantProfile?.university = university
                user.instructorProfile?.company = company
                user.instructorProfile?.year = year
                user.participantProfile?.accepted = boolean_accepted
                user.instructorProfile?.updatedAt = LocalDateTime.now()
                user.participantProfile?.updatedAt = LocalDateTime.now()
            }
        } else throw RoleTypeException("Role should be participant or instructor.")

        return userRepository.save(user)
    }
    fun getUserById(id: Long): User {
        return userRepository.findByIdOrNull(id) ?: throw UserNotFoundException("User is not found.")
    }

    fun beParticipant(participantRequest: ParticipantDto.ParticipantRequest, user: User) : User {
        if (user.roles.indexOf(',') == -1) {
            if(!user.roles.equals("instructor")) {
                throw UserAlreadyParticipantException("User is not a instructor.")
            }
        } else {
            val rolesSet = user.roles.split(",").toSet()
            if(rolesSet == setOf<String>("instructor", "participant")) {
                throw UserAlreadyParticipantException("User is already a participant.")
            }
        }
        user.roles = "instructor,participant"
        var university = participantRequest.university
        var accepted = participantRequest.accepted
        if(university == null) university = ""
        if(accepted == null) accepted = "true"
        val boolean_accepted = accepted.lowercase().toBoolean()
        participant = participantRepository.save(ParticipantProfile(university, boolean_accepted))
        user.participantProfile = participant

        return userRepository.save(user)
    }
}
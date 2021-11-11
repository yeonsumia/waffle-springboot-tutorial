package com.wafflestudio.seminar.domain.user.service

import com.wafflestudio.seminar.domain.user.dto.ParticipantDto
import com.wafflestudio.seminar.domain.user.dto.UserDto
import com.wafflestudio.seminar.domain.user.exception.UserAlreadyExistsException
import com.wafflestudio.seminar.domain.user.model.InstructorProfile
import com.wafflestudio.seminar.domain.user.model.ParticipantProfile
import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.user.repository.InstructorRepository
import com.wafflestudio.seminar.domain.user.repository.ParticipantRepository
import com.wafflestudio.seminar.domain.user.repository.UserRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.MediaType
import org.springframework.mock.env.MockEnvironment
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import java.lang.Boolean.parseBoolean
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    @Mock
    private lateinit var userRepository : UserRepository

    @Mock
    private lateinit var instructorRepository: InstructorRepository

    @Mock
    private lateinit var participantRepository: ParticipantRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var participant: ParticipantProfile

    @Mock
    private lateinit var instructor: InstructorProfile

    @Test
    fun signup() {
        val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
        val signupRequest1 : UserDto.SignupRequest = UserDto.SignupRequest("yeonsumia@snu.ac.kr", "hsJeon", "1234", "instructor", null, null, 1, "wafflestudio")
        val signupRequest2 : UserDto.SignupRequest = UserDto.SignupRequest("yeonsumia@snu.ac.kr", "hs", "1234", "instructor", null, null, null, null)
        instructor = InstructorProfile("wafflestudio", 1)
        val userService = UserService(userRepository, passwordEncoder, participantRepository, instructorRepository, participant, instructor)

        //mocking
        given(instructorRepository.save(any())).willReturn(instructor)
        given(userRepository.save(any())).willReturn(User("yeonsumia@snu.ac.kr", "hsJeon", "1234", "instructor", null, instructor))

        //when
        val user : User = userService.signup(signupRequest1)
        assertEquals(user.email, signupRequest1.email)
        assertEquals(user.name, signupRequest1.name)
        assertEquals(user.instructorProfile!!.company, signupRequest1.company)
        assertEquals(user.instructorProfile!!.year, signupRequest1.year)

        assertThrows(UserAlreadyExistsException::class.java) {
            given(userRepository.existsByEmail(signupRequest2.email!!)).willReturn(signupRequest1.email == signupRequest2.email)
            val user : User = userService.signup(signupRequest2)
        }
    }

    @Test
    fun modifyUser() {
        val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
        instructor = InstructorProfile("wafflestudio", 1)
        val user1 = User("yeonsumia@snu.ac.kr", "hsJeon", "1234", "instructor", null, instructor)
        val putRequest1 : UserDto.PutRequest = UserDto.PutRequest("yeonsumia@snu.ac.kr", "yeonsumia", "1234", "instructor", null, 3, "SNU")

        val userService = UserService(userRepository, passwordEncoder, participantRepository, instructorRepository, participant, instructor)

        //mocking
        given(userRepository.save(any())).willReturn(User("yeonsumia@snu.ac.kr", "yeonsumia", "1234", "instructor", null, InstructorProfile("SNU", 3)))

        //when
        val user : User = userService.modifyUser(putRequest1, user1)

        assertEquals(user.email, putRequest1.email)
        assertEquals(user.name, putRequest1.name)
        assertEquals(user.instructorProfile!!.company, putRequest1.company)
        assertEquals(user.instructorProfile!!.year, putRequest1.year)
    }

    @Test
    fun beParticipant() {
        val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
        instructor = InstructorProfile("wafflestudio", 1)
        val user1 = User("yeonsumia@snu.ac.kr", "hsJeon", "1234", "instructor", null, instructor)
        val participantRequest1 : ParticipantDto.ParticipantRequest = ParticipantDto.ParticipantRequest("SNU", "false")

        val userService = UserService(userRepository, passwordEncoder, participantRepository, instructorRepository, participant, instructor)

        //mocking
        given(userRepository.save(any())).willReturn(User("yeonsumia@snu.ac.kr", "yeonsumia", "1234", "instructor,participant", ParticipantProfile("SNU", false), instructor))

        //when
        val user : User = userService.beParticipant(participantRequest1, user1)

        assertEquals(user.participantProfile!!.university, participantRequest1.university)
        assertEquals(user.participantProfile!!.accepted, parseBoolean(participantRequest1.accepted))
    }
}
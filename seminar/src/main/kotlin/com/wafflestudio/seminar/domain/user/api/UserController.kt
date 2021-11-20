package com.wafflestudio.seminar.domain.user.api

import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.user.service.UserService
import com.wafflestudio.seminar.domain.user.dto.UserDto
import com.wafflestudio.seminar.domain.user.dto.ParticipantDto
import com.wafflestudio.seminar.global.auth.CurrentUser
import com.wafflestudio.seminar.global.auth.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/v1")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @PostMapping("/users/")
    fun signup(@Valid @RequestBody signupRequest: UserDto.SignupRequest): ResponseEntity<UserDto.Response> {
        val user = userService.signup(signupRequest)
        return ResponseEntity.noContent().header("Authentication", jwtTokenProvider.generateToken(user.email)).build()
    }

    @GetMapping("/users/me/")
    fun getCurrentUser(@CurrentUser user: User): ResponseEntity<UserDto.Response> {
        return ResponseEntity.ok(UserDto.Response(user))
    }

    @PutMapping("/user/me/")
    fun putCurrentUser(@Valid @RequestBody putRequest: UserDto.PutRequest, @CurrentUser user: User): ResponseEntity<UserDto.Response> {
        val modified_user = userService.modifyUser(putRequest, user)
        return ResponseEntity.ok(UserDto.Response(modified_user))
    }

    @PostMapping("/user/participant/")
    fun beParticipant(@Valid @RequestBody participantRequest: ParticipantDto.ParticipantRequest, @CurrentUser user: User): ResponseEntity<UserDto.Response> {
        val modified_user = userService.beParticipant(participantRequest, user)
        return ResponseEntity(UserDto.Response(modified_user), HttpStatus.CREATED)
    }

    @GetMapping("/users/{id}/")
    fun getUser(@PathVariable("id") id: Long): ResponseEntity<UserDto.Response> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(UserDto.Response(user))
    }
}

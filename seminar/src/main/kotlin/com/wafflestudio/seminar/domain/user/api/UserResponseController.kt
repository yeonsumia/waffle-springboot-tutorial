package com.wafflestudio.seminar.domain.user.api

import com.wafflestudio.seminar.domain.user.dto.UserResponseDto

import com.wafflestudio.seminar.domain.user.service.UserService
import com.wafflestudio.seminar.domain.user.exception.UserNotFoundException
import com.wafflestudio.seminar.domain.user.model.UserResponse
import com.wafflestudio.seminar.domain.user.repository.UserRepository

import org.modelmapper.ModelMapper
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import org.springframework.validation.BindingResult

@RestController
@RequestMapping("/api/v1/user")
class UserResponseController(
    private val userService: UserService,
    private val modelMapper: ModelMapper,
    private val userRepository: UserRepository,
) {

    @PostMapping("/")
    fun addUserResponse(
        @ModelAttribute @Valid body: UserResponseDto.CreateRequest, bindingResult: BindingResult

    ): ResponseEntity<UserResponse> {
        //TODO: API 생성
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().build()
        }
        val newUserResponse = modelMapper.map(body, UserResponse::class.java)
        userRepository.save(newUserResponse)
        return ResponseEntity(newUserResponse, HttpStatus.CREATED)
    }

    @GetMapping("/me/")
    fun getUserResponse(
        @RequestHeader("User-Id") userId: Long
    ) : ResponseEntity<UserResponseDto.Response> {
        return try {
            var userResponse = userService.getUserById(userId)
            val responseBody = modelMapper.map(userResponse, UserResponseDto.Response::class.java)
            ResponseEntity.ok(responseBody)
        } catch (e: UserNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

}
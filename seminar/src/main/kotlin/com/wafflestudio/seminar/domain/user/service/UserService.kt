package com.wafflestudio.seminar.domain.user.service

import com.wafflestudio.seminar.domain.user.dto.UserResponseDto
import com.wafflestudio.seminar.domain.user.exception.UserNotFoundException
import com.wafflestudio.seminar.domain.user.model.UserResponse
import com.wafflestudio.seminar.domain.user.repository.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun getUserById(id: Long): UserResponse {
        return userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()
    }
}
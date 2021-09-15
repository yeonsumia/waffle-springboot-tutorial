package com.wafflestudio.seminar.domain.user.repository

import com.wafflestudio.seminar.domain.user.model.UserResponse
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserResponse, Long?> {
    fun findByNameEquals(name: String): UserResponse?
}
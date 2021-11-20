package com.wafflestudio.seminar.domain.seminar.api

import com.wafflestudio.seminar.domain.seminar.service.SeminarService
import com.wafflestudio.seminar.domain.seminar.dto.SeminarDto
import com.wafflestudio.seminar.domain.seminar.dto.ShowSeminarDto
import com.wafflestudio.seminar.global.auth.CurrentUser
import com.wafflestudio.seminar.domain.user.model.User
import com.wafflestudio.seminar.domain.user.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import org.springframework.http.HttpStatus
import com.wafflestudio.seminar.global.common.dto.ListResponse

@RestController
@RequestMapping("/api/v1")
class SeminarController(
        private val seminarService: SeminarService,
        private var seminarList: List<ShowSeminarDto.Response>
) {
    @PostMapping("/seminars/")
    fun addSeminar(@Valid @RequestBody seminarRequest: SeminarDto.SeminarRequest, @CurrentUser user: User): ResponseEntity<SeminarDto.Response> {
        val seminar = seminarService.addSeminar(seminarRequest, user)
        return ResponseEntity(SeminarDto.Response(seminar), HttpStatus.CREATED)
    }

    @PutMapping("/seminars/{id}/")
    fun modifySeminar(@PathVariable("id") id: Long, @Valid @RequestBody seminarRequest: SeminarDto.SeminarRequest?, @CurrentUser user: User): ResponseEntity<SeminarDto.Response> {
        val seminar = seminarService.modifySeminar(id, seminarRequest, user)
        return ResponseEntity.ok(SeminarDto.Response(seminar))
    }

    @PostMapping("/seminars/{id}/user/")
    fun joinSeminar(@PathVariable("id") id: Long, @Valid @RequestBody joinSeminarRequest: SeminarDto.JoinSeminarRequest, @CurrentUser user: User): ResponseEntity<SeminarDto.Response> {
        val seminar = seminarService.joinSeminar(id, joinSeminarRequest, user)
        return ResponseEntity(SeminarDto.Response(seminar), HttpStatus.CREATED)
    }

    @DeleteMapping("/seminars/{id}/user/me/")
    fun dropSeminar(@PathVariable("id") id: Long, @CurrentUser user: User): ResponseEntity<SeminarDto.Response> {
        val seminar = seminarService.dropSeminar(id, user)
        return ResponseEntity.ok(SeminarDto.Response(seminar))
    }

    @GetMapping("/seminar/{id}/")
    fun getSeminar(@PathVariable("id") id: Long): ResponseEntity<SeminarDto.Response> {
        val seminar = seminarService.getSeminarById(id)
        return ResponseEntity.ok(SeminarDto.Response(seminar))
    }

    @GetMapping("/seminar/")
    fun getSeminars(@RequestParam(value="name", required=false) seminarName : String?,
                    @RequestParam(value="order", required=false) seminarOrder : String?
    ): ResponseEntity<ListResponse<ShowSeminarDto.Response>> {
        if(seminarName != null){
            if(seminarOrder != null && seminarOrder == "earliest"){
                seminarList = seminarService.getSeminarsByName(seminarName, false)
            } else seminarList = seminarService.getSeminarsByName(seminarName, true)
        }
        else {
            if(seminarOrder != null && seminarOrder == "earliest"){
                seminarList = seminarService.getAllSeminars(false)
            } else seminarList = seminarService.getAllSeminars(true)
        }
        return ResponseEntity.ok(ListResponse(seminarList))
    }

}

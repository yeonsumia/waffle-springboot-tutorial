package com.wafflestudio.seminar.domain.survey.api

import com.wafflestudio.seminar.domain.survey.dto.SurveyResponseDto

import com.wafflestudio.seminar.domain.os.exception.OsNotFoundException
import com.wafflestudio.seminar.domain.os.service.OperatingSystemService
import com.wafflestudio.seminar.domain.os.model.OperatingSystem

import com.wafflestudio.seminar.domain.survey.exception.SurveyNotFoundException
import com.wafflestudio.seminar.domain.survey.model.SurveyResponse
import com.wafflestudio.seminar.domain.survey.service.SurveyResponseService
import com.wafflestudio.seminar.domain.survey.repository.SurveyResponseRepository

import com.wafflestudio.seminar.domain.user.service.UserService
import com.wafflestudio.seminar.domain.user.exception.UserNotFoundException

import org.modelmapper.ModelMapper
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import org.springframework.validation.BindingResult

@RestController
@RequestMapping("/api/v1/result")
class SurveyResponseController(
    private val surveyResponseService: SurveyResponseService,
    private val modelMapper: ModelMapper,
    private val surveyResponseRepository: SurveyResponseRepository,
    private val operatingSystemService: OperatingSystemService,
    private val userService: UserService
) {
    @GetMapping("/")
    fun getSurveyResponses(@RequestParam(required = false) os: String?): ResponseEntity<List<SurveyResponseDto.Response>> {
        return try {
            val surveyResponses =
                if (os != null) surveyResponseService.getSurveyResponsesByOsName(os)
                else surveyResponseService.getAllSurveyResponses()
            val responseBody = surveyResponses.map { modelMapper.map(it, SurveyResponseDto.Response::class.java) }
            ResponseEntity.ok(responseBody)
        } catch (e: OsNotFoundException) {
            ResponseEntity.notFound().build()
        }
        // AOP를 적용해 exception handling을 따로 하도록 고쳐보셔도 됩니다.
    }

    @GetMapping("/{id}/")
    fun getSurveyResponse(@PathVariable("id") id: Long): ResponseEntity<SurveyResponseDto.Response> {
        return try {
            val surveyResponse = surveyResponseService.getSurveyResponseById(id)
            val responseBody = modelMapper.map(surveyResponse, SurveyResponseDto.Response::class.java)
            ResponseEntity.ok(responseBody)
        } catch (e: SurveyNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/")
    fun addSurveyResponse(
        @ModelAttribute @Valid body: SurveyResponseDto.CreateRequest, bindingResult: BindingResult,
        @RequestHeader("User-Id") userId: Long
    ): ResponseEntity<SurveyResponse> {
        //TODO: API 생성
//            val newSurveyResponse = modelMapper.map(body, SurveyResponse::class.java)
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().build()
        }
        return try {
            val newSurveyResponse = SurveyResponse(
                os = operatingSystemService.getOperatingSystemByName(body.os),
                springExp = body.spring_exp,
                rdbExp = body.rdb_exp,
                programmingExp = body.programming_exp,
                major = body.major,
                grade = body.grade,
                backendReason = body.backend_reason,
                waffleReason = body.waffle_reason,
                somethingToSay = body.something_to_say,
                user = userService.getUserById(userId)
            )
            surveyResponseRepository.save(newSurveyResponse)
            ResponseEntity(newSurveyResponse, HttpStatus.CREATED)
        } catch (e: OsNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: UserNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

//    @PatchMapping("/{id}/")
//    fun modifySurveyResponseWithId(@ModelAttribute @Valid body: SurveyResponseDto.ModifyRequest): SurveyResponseDto.Response {
//        //TODO: API 생성
//        return SurveyResponseDto.Response()
//    }
}

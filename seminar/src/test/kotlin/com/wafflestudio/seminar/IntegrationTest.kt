package com.wafflestudio.seminar.domain

import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import javax.transaction.Transactional

/*
  원래는 integration test도 패키지 분리하는 것이 좋습니다.

  또 Unit test 위주로 작성하고 전체적인 흐름을 integration test에서 하는 것이 좋으나
  Unit test의 경우 각 인원의 서버 상세 구현에 맞춰야 하고
  이후 채점의 편의를 위해 하나의 파일에 모든 테스트케이스를 두는 방식을 사용했습니다.
  Integration test뿐 아니라 Unit test도 작성해주시면 좋겠습니다.
  이슈에 질문을 올리시면 유닛테스트도 아주 간단한 예시 올려보겠습니다.

  실제 과제 채점시에는 배포까지 같이 화인할 예정이므로 해당 테스트 파일을 쓰지 않습니다.
  배포된 사이트의 url만 받아 아래 나와있는 테스트 로직 + a 를 api로 날려 진행할 예정이므로 서버가 잘 동작하도록 유지시켜 주시기 바랍니다.
 */
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
internal class IntegrationTest(@Autowired private val mockMvc: MockMvc) {
    @BeforeEach
    fun `회원가입`() {
        signupAsParticipantUser("hankp").andExpect {
            status { isNoContent() }
            header { exists("Authentication") }
        }

        signupAsInstructorUser("hanki").andExpect {
            status { isNoContent() }
            header { exists("Authentication") }
        }
    }



    // Test에서 호출한 api들은 실제로 저장이 되면 안되는 데이터들이다.
    // 따라서 모든 테스트케이스에 Transactional annotation 추가
    // 이 경우 하나의 테스트케이스가 끝날 때마다 해당 테스트에서의 동작들이 모두 rollback 된다.
    @Test
    @Transactional
    fun `회원 가입 정상 동작 검증`() {
        signupAsParticipantUser("hankp2").andExpect {
            status { isNoContent() }
            header { exists("Authentication") }
        }
    }

    @Test
    @Transactional
    fun `중복 이메일 가입 불가능 조건 검증`() {
        signupAsParticipantUser("hankp").andExpect {
            status { isConflict() }
        }
    }

    @Test
    @Transactional
    fun `회원 가입 요청 body 오류`() {
        signup(
            """
                {
                    "name": "wrong_role",
                    "password": "password",
                    "email": "wrong@snu.ac.kr",
                    "role": "wrong_role",
                    "university": "서울대학교"
                }
            """.trimIndent()
        ).andExpect { status { isBadRequest() } }
        signup(
            """
                {
                    "name": "no_role",
                    "password": "password",
                    "email": "no_role@snu.ac.kr",
                    "university": "서울대학교"
                }
            """.trimIndent()
        ).andExpect { status { isBadRequest() } }
    }

    @Test
    private fun login(name: String): String? {
        return mockMvc.post("/api/v1/users/signin/") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content =
                """
                        {
                            "email": "${name}@snu.ac.kr",
                            "password": "${name}"
                        }
                """.trimIndent()
        }.andReturn().response.getHeader("Authentication")
    }

    private fun signupAsInstructorUser(name: String): ResultActionsDsl {
        val body =
            """
                {
                    "password": "${name}",
                    "name": "${name}",
                    "email": "${name}@snu.ac.kr",
                    "role": "instructor",
                    "company": "wafflestudio",
                    "year": "1"
                }
            """.trimIndent()
        return signup(body)
    }

    private fun signupAsParticipantUser(name: String): ResultActionsDsl {
        val body =
            """
                {
                    "password": "${name}",
                    "name": "${name}",
                    "email": "${name}@snu.ac.kr",
                    "role": "participant"
                }
            """.trimIndent()
        return signup(body)
    }

    private fun signup(body: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/users/") {
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
    }

    // 계속 업데이트 예정

    @Test
    @Transactional
    fun `본인 정보 확인 성공`() {
        val authentication = login("hanki")
        mockMvc.get("/api/v1/users/me/") {
            header("Authentication", authentication!!)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
            .andExpect { content { json("""
                {
                    "id": 2,
                    "email": "hanki@snu.ac.kr",
                    "name": "hanki",
                    "roles": "instructor",
                    "participantProfile": null,
                    "instructorProfile": {
                        "id": 1,
                        "company": "wafflestudio",
                        "year": 1,
                        "charge": null
                    }
                }
            """.trimIndent()) } }
    }

    @Test
    @Transactional
    fun `회원 정보 확인 성공`() {
        val authentication = login("hanki")
        mockMvc.get("/api/v1/users/2/") {
            header("Authentication", authentication!!)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
            .andExpect { content { json("""
                {
                    "id": 2,
                    "email": "hanki@snu.ac.kr",
                    "name": "hanki",
                    "roles": "instructor",
                    "participantProfile": null,
                    "instructorProfile": {
                        "id": 1,
                        "company": "wafflestudio",
                        "year": 1,
                        "charge": null
                    }
                }
            """.trimIndent()) } }
    }

    @Test
    @Transactional
    fun `회원 정보 확인 실패`() {
        val authentication = login("hanki")
        mockMvc.get("/api/v1/users/3/") {
            header("Authentication", authentication!!)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isNotFound() } }
    }

    @Test
    @Transactional
    fun `참여자 권한 부여 성공`() {
        val authentication = login("hanki")
        val body = """
                {
                    "university": "SNU"
                }
            """
        mockMvc.post("/api/v1/user/participant/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }
            .andExpect { content { json(
                """
                {
                    "id": 2,
                    "email": "hanki@snu.ac.kr",
                    "name": "hanki",
                    "roles": "instructor,participant",
                    "participantProfile": {
                        "id": 2,
                        "university": "SNU",
                        "accepted": true,
                        "seminars": []
                    },
                    "instructorProfile": {
                        "id": 1,
                        "company": "wafflestudio",
                        "year": 1,
                        "charge": null
                    }
                }
            """.trimIndent()) } }
    }

    @Test
    @Transactional
    fun `참여자 권한 부여 실패(이미 참여자)`() {
        val authentication = login("hanki")
        val body = """
                {
                    "university": "SNU"
                }
            """
        mockMvc.post("/api/v1/user/participant/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        mockMvc.post("/api/v1/user/participant/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    @Transactional
    fun `회원 정보 수정 성공`() {
        val authentication = login("hanki")
        val body = """
                {
                    "password": "hanki",
                    "name": "hanki",
                    "email": "hanki@snu.ac.kr",
                    "role": "instructor",
                    "company": "waffle",
                    "year": 3,
                    "university": "SNU"
                }
            """.trimIndent()
        mockMvc.put("/api/v1/user/me/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
            .andExpect { content { json(
                """
                {
                    "id": 2,
                    "email": "hanki@snu.ac.kr",
                    "name": "hanki",
                    "roles": "instructor",
                    "participantProfile": null,
                    "instructorProfile": {
                        "id": 1,
                        "company": "waffle",
                        "year": 3,
                        "charge": null
                    }
                }
            """.trimIndent()) } }

        val emptyBody = "{}"
        mockMvc.put("/api/v1/user/me/") {
            header("Authentication", authentication!!)
            content = (emptyBody)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
    }

    @Test
    @Transactional
    fun `회원 정보 수정 실패(연도)`() {
        val authentication = login("hankp")
        val body = """
                {
                    "university": "SNU",
                    "year": 0
                }
            """.trimIndent()
        mockMvc.put("/api/v1/user/me/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    @Transactional
    fun `세미나 생성 성공`() {
        val authentication = login("hanki")
        val body = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }
            .andExpect { content { json(
                """
                {
                    "id": 1,
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30",
                    "online": true,
                    "instructors": [
                        {
                            "id": 2,
                            "name": "hanki",
                            "email": "hanki@snu.ac.kr",
                            "company": "wafflestudio"
                        }
                    ],
                    "participants": []
                }
            """.trimIndent()
            ) } }
    }

    @Test
    @Transactional
    fun `세미나 생성 실패(이름 비었음)`() {
        val authentication = login("hanki")
        val body = """
                {
                    "name": "",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            accept = (MediaType.APPLICATION_JSON)
            contentType = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    @Transactional
    fun `세미나 생성 실패(시간 오류)`() {
        val authentication = login("hanki")
        val body = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "25:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    @Transactional
    fun `세미나 생성 실패(진행자 아님)`() {
        val authentication = login("hankp")
        val body = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isForbidden() } }
    }

    @Test
    @Transactional
    fun `세미나 정보 수정 성공`() {
        val authentication = login("hanki")
        val body = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        val putBody = """
                {
                    "name": "iOS",
                    "capacity": 5,
                    "count": 3,
                    "time": "20:30"
                }
        """.trimIndent()
        mockMvc.put("/api/v1/seminars/1/") {
            header("Authentication", authentication!!)
            content = (putBody)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
            .andExpect { content { json(
                """
                {
                    "id": 1,
                    "name": "iOS",
                    "capacity": 5,
                    "count": 3,
                    "time": "20:30",
                    "online": true,
                    "instructors": [
                        {
                            "id": 2,
                            "name": "hanki",
                            "email": "hanki@snu.ac.kr",
                            "company": "wafflestudio"
                        }
                    ],
                    "participants": []
                }
            """.trimIndent()
            ) } }
    }

    @Test
    @Transactional
    fun `세미나 정보 수정 실패(인원 수)`() {
        val authentication = login("hanki")
        val body = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        val authenticationP = login("hankp")
        val joinBody = """
            {
                "role": "participant"
            }
        """.trimIndent()
        mockMvc.post("/api/v1/seminars/1/user/") {
            header("Authentication", authenticationP!!)
            content = (joinBody)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        // change capacity
        val putBody = """
                {
                    "name": "iOS",
                    "capacity": 0,
                    "count": 1,
                    "time": "15:30"
                }
        """.trimIndent()
        mockMvc.put("/api/v1/seminars/1/") {
            header("Authentication", authentication!!)
            content = (putBody)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    @Transactional
    fun `세미나 정보 확인 성공(param 까지 확인)`() {
        val authentication = login("hanki")
        val authenticationP = login("hankp")
        val body1 = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        val body2 = """
                {
                    "name": "springboot",
                    "capacity": 5,
                    "count": 2,
                    "time": "17:30"
                }
            """.trimIndent()
        val body3 = """
                {
                    "role": "participant"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body1)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body2)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        mockMvc.get("/api/v1/seminar/1/") {
            header("Authentication", authentication!!)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
            .andExpect { content { json(
                """
                {
                    "id": 1,
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30",
                    "online": true,
                    "instructors": [
                        {
                            "id": 2,
                            "name": "hanki",
                            "email": "hanki@snu.ac.kr",
                            "company": "wafflestudio"
                        }
                    ],
                    "participants": []
                }
            """.trimIndent()
            ) } }

        // search get
        mockMvc.post("/api/v1/seminars/2/user/") {
            header("Authentication", authenticationP!!)
            content = (body3)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        mockMvc.get("/api/v1/seminar/") {
            header("Authentication", authenticationP!!)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
            .andExpect { content { json(
                """
                {
                    "count": 2,
                    "results": [
                        {
                            "id": 2,
                            "name": "springboot",
                            "instructors": [
                                {
                                    "id": 2,
                                    "name": "hanki",
                                    "email": "hanki@snu.ac.kr",
                                    "company": "wafflestudio"
                                }
                            ],
                            "participantCount": 1
                        },
                        {
                            "id": 1,
                            "name": "iOS",
                            "instructors": [
                                {
                                    "id": 2,
                                    "name": "hanki",
                                    "email": "hanki@snu.ac.kr",
                                    "company": "wafflestudio"
                                }
                            ],
                            "participantCount": 0
                        }
                    ]
                }
            """.trimIndent()
            ) } }

        mockMvc.get("/api/v1/seminar/?name=spring") {
            header("Authentication", authenticationP!!)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
            .andExpect { content { json(
                """
                {
                    "count": 1,
                    "results": [
                        {
                            "id": 2,
                            "name": "springboot",
                            "instructors": [
                                {
                                    "id": 2,
                                    "name": "hanki",
                                    "email": "hanki@snu.ac.kr",
                                    "company": "wafflestudio"
                                }
                            ],
                            "participantCount": 1
                        }
                    ]
                }
            """.trimIndent()
            ) } }

        mockMvc.get("/api/v1/seminar/?order=earliest") {
            header("Authentication", authenticationP!!)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
            .andExpect { content { json(
                """
                {
                    "count": 2,
                    "results": [
                        {
                            "id": 1,
                            "name": "iOS",
                            "instructors": [
                                {
                                    "id": 2,
                                    "name": "hanki",
                                    "email": "hanki@snu.ac.kr",
                                    "company": "wafflestudio"
                                }
                            ],
                            "participantCount": 0
                        },
                        {
                            "id": 2,
                            "name": "springboot",
                            "instructors": [
                                {
                                    "id": 2,
                                    "name": "hanki",
                                    "email": "hanki@snu.ac.kr",
                                    "company": "wafflestudio"
                                }
                            ],
                            "participantCount": 1
                        }
                    ]
                }
            """.trimIndent()
            ) } }
    }

    @Test
    @Transactional
    fun `세미나 정보 확인 실패`() {
        val authentication = login("hanki")

        mockMvc.get("/api/v1/seminar/1/") {
            header("Authentication", authentication!!)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isNotFound() } }
    }

    @Test
    @Transactional
    fun `세미나 참여 성공`() {
        val authentication = login("hanki")
        val body = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        val joinBody = """
            {
                "role": "participant"
            }
        """.trimIndent()
        val authenticationP = login("hankp")
        mockMvc.post("/api/v1/seminars/1/user/") {
            header("Authentication", authenticationP!!)
            content = (joinBody)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }
            .andExpect {
                jsonPath("$.id").value(1)
                jsonPath("$.name").value("iOS")
                jsonPath("$.capacity").value(3)
                jsonPath("$.time").value("15:30")
                jsonPath("$.online").value(true)
                jsonPath("$.instructors[0].id").value(1)
                jsonPath("$.instructors[0].name").value("hanki")
                jsonPath("$.instructors[0].email").value("hanki@snu.ac.kr")
                jsonPath("$.instructors[0].company").value("")
                jsonPath("$.participants[0].id").value(1)
                jsonPath("$.participants[0].name").value("hankp")
                jsonPath("$.participants[0].email").value("hankp@snu.ac.kr")
                jsonPath("$.participants[0].university").value("")
                jsonPath("$.participants[0].joinedAt", notNullValue())
                jsonPath("$.participants[0].isActive").value(true)
                jsonPath("$.participants[0].droppedAt").value(null)
            }
    }

    @Test
    @Transactional
    fun `세미나 참여 실패(참여자 자격 없음)`() {
        val authentication = login("hanki")
        val body = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        val joinBody = """
            {
                "role": "participant"
            }
        """.trimIndent()

        mockMvc.post("/api/v1/seminars/1/user/") {
            header("Authentication", authentication!!)
            content = (joinBody)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isForbidden() } }
    }

    @Test
    @Transactional
    fun `세미나 탈퇴 성공`() {
        val authentication = login("hanki")
        val body = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        val joinBody = """
            {
                "role": "participant"
            }
        """.trimIndent()

        val authenticationP = login("hankp")
        mockMvc.post("/api/v1/seminars/1/user/") {
            header("Authentication", authenticationP!!)
            content = (joinBody)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        mockMvc.delete("/api/v1/seminars/1/user/me/") {
            header("Authentication", authenticationP!!)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
            .andExpect {
                jsonPath("$.participants[0].isActive").value(false)
                jsonPath("$.participants[0].isActive", notNullValue())
            }
        // 이미 탈퇴한 상황
        mockMvc.delete("/api/v1/seminars/1/user/me/") {
            header("Authentication", authenticationP!!)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isOk() } }
    }

    @Test
    @Transactional
    fun `세미나 탈퇴 실패(진행자)`() {
        val authentication = login("hanki")
        val body = """
                {
                    "name": "iOS",
                    "capacity": 3,
                    "count": 1,
                    "time": "15:30"
                }
            """.trimIndent()
        mockMvc.post("/api/v1/seminars/") {
            header("Authentication", authentication!!)
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        val joinBody = """
            {
                "role": "participant"
            }
        """.trimIndent()

        val authenticationP = login("hankp")
        mockMvc.post("/api/v1/seminars/1/user/") {
            header("Authentication", authenticationP!!)
            content = (joinBody)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isCreated() } }

        mockMvc.delete("/api/v1/seminars/1/user/me/") {
            header("Authentication", authentication!!)
            accept = (MediaType.APPLICATION_JSON)
        }
            .andExpect { status { isForbidden() } }
    }


}

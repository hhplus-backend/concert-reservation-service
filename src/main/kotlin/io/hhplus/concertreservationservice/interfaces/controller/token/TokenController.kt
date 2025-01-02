package io.hhplus.concertreservationservice.interfaces.controller.token

import io.hhplus.concertreservationservice.interfaces.response.ApiResponse
import io.hhplus.concertreservationservice.interfaces.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/queue-token")
class TokenController {
    // 유저 토큰 발급 API
    // 1.	waiting 토큰 발급 성공:
    // 	•	요청: { "userId": "1" }
    // 2.	active 토큰 발급 성공:
    // 	•	요청: { "userId": "2" }
    // 3.	토큰 발급 실패:
    // 	•	요청: { "userId": "3" }
    // 4.	user invalid:
    // 	•	요청: { "userId": "4" }
    // 5.	user token issued fail:
    // 	•	요청: { "userId": "4" }
    @PostMapping("")
    fun createQueueToken(
        @RequestBody request: QueueTokenRequest?,
    ): ResponseEntity<*> {
        return try {
            if (request == null || request.userId.isBlank()) {
                throw IllegalArgumentException("Invalid request data")
            }

            val response =
                when (request.userId) {
                    "1" -> {
                        val queueTokenResponse = QueueTokenResponse(queueToken = "active-token")
                        ApiResponse(
                            success = true,
                            code = "SUCCESS_01",
                            message = "Success",
                            data = queueTokenResponse,
                        )
                    }

                    "2" -> {
                        val queueTokenResponse = QueueTokenResponse(queueToken = "waiting-token")
                        ApiResponse(
                            success = true,
                            code = "SUCCESS_01",
                            message = "Success",
                            data = queueTokenResponse,
                        )
                    }

                    "3" -> throw IllegalArgumentException("Invalid request data")
                    "4" -> throw NoSuchElementException("User not found")
                    else -> throw Exception("toke issue fail")
                }

            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse(
                    code = "FAIL_01",
                    message = "Request is invalid",
                    data = ex.message,
                ),
            )
        } catch (ex: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(
                    code = "USER_ERROR_01",
                    message = "User not found",
                    data = ex.message,
                ),
            )
        } catch (ex: Exception) {
            // 예외 케이스: 서버 내부 오류
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse(
                    code = "TOKEN_ERROR_01",
                    message = "Token issue fail",
                    data = ex.message,
                ),
            )
        }
    }

    // 유저 토큰 조회 API
    // 1.	active 토큰 조회 성공:
    // 	•	헤더: USER-TOKEN: Bearer valid-token-1
    // 2.	waiting 토큰 조회 성공:
    // 	•	헤더: USER-TOKEN: Bearer valid-token-2
    // 3.	만료된 토큰:
    // 	•	헤더: USER-TOKEN: Bearer expired-token-1
    // 4.	토큰형식에 어긋남:
    // 	•	헤더: USER-TOKEN: Bearer12345
    @GetMapping("")
    fun getQueueTokenStatus(
        @RequestHeader("USER-TOKEN") token: String?,
    ): ResponseEntity<*> {
        return try {
            if (token.isNullOrBlank() || !token.startsWith("Bearer ")) {
                throw IllegalArgumentException("user token is invalid or expired")
            }

            val response =
                when (token) {
                    "Bearer valid-token-1" -> {
                        val queueInfo = QueueTokenStatusResponse(status = "ACTIVE")
                        ApiResponse(
                            success = true,
                            code = "SUCCESS_01",
                            message = "Success",
                            data = queueInfo,
                        )
                    }

                    "Bearer valid-token-2" -> {
                        val queueInfo = QueueTokenStatusResponse(status = "WAITING")
                        ApiResponse(
                            success = true,
                            code = "SUCCESS_01",
                            message = "Success",
                            data = queueInfo,
                        )
                    }

                    "Bearer expired-token-1" -> throw IllegalArgumentException("user token is invalid or expired")
                    else -> throw Exception("fetch token failed")
                }

            ResponseEntity.ok(response)
        } catch (ex: IllegalArgumentException) {
            // 예외 케이스: 헤더 누락 또는 유효하지 않음
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse(
                    code = "FAIL_01",
                    message = "Request is invalid",
                    data = ex.message,
                ),
            )
        } catch (ex: Exception) {
            // 예외 케이스: 서버 내부 오류
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse(
                    code = "TOKEN_ERROR_01",
                    message = "fetch token failed",
                    data = ex.message,
                ),
            )
        }
    }
}

data class QueueTokenRequest(
    val userId: String,
)

data class QueueTokenResponse(
    val queueToken: String,
)

data class QueueTokenStatusResponse(
    val status: String,
)

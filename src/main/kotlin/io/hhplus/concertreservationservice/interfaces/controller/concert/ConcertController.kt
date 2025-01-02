package io.hhplus.concertreservationservice.interfaces.controller.concert

import io.hhplus.concertreservationservice.interfaces.response.ApiResponse
import io.hhplus.concertreservationservice.interfaces.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/concert")
class ConcertController {
    private val validConcertSchedules =
        mapOf(
            1L to
                mapOf(
                    101L to
                        mapOf(
                            "2025-01-01" to listOf(1, 2, 3),
                            "2025-01-02" to listOf(4, 5),
                        ),
                ),
        )

    private val concerts =
        listOf(
            ConcertResponse(
                concertId = 1L,
                title = "A 콘서트",
            ),
            ConcertResponse(
                concertId = 2L,
                title = "B 콘서트",
            ),
        )

    private val concertSchedules =
        listOf(
            ConcertWithScheduleResponse(
                concertId = 1L,
                title = "A 콘서트",
                schedules =
                    listOf(
                        ConcertScheduleResponse(
                            scheduleId = 101L,
                            performanceDate = "2025-01-01",
                            startTime = "18:00",
                            endTime = "20:00",
                            place =
                                PlaceInfo(
                                    name = "LG아트센터",
                                    availableSeatCount = 500,
                                ),
                        ),
                        ConcertScheduleResponse(
                            scheduleId = 102L,
                            performanceDate = "2025-01-02",
                            startTime = "19:00",
                            endTime = "21:00",
                            place =
                                PlaceInfo(
                                    name = "우리금융아트홀",
                                    availableSeatCount = 450,
                                ),
                        ),
                    ),
            ),
            ConcertWithScheduleResponse(
                concertId = 2L,
                title = "Spring Blossom Concert",
                schedules =
                    listOf(
                        ConcertScheduleResponse(
                            scheduleId = 201L,
                            performanceDate = "2025-03-15",
                            startTime = "17:00",
                            endTime = "19:00",
                            place =
                                PlaceInfo(
                                    name = "LG아트센터",
                                    availableSeatCount = 500,
                                ),
                        ),
                    ),
            ),
        )

    private val validTokens = setOf("Bearer valid-token-1", "Bearer valid-token-2")

    // 콘서트 목록 조회 API
    // 1. 정상 응답:
    //  • 경로: /api/concerts
    // 	• 헤더: USER-TOKEN: Bearer valid-token-1
    //  • 응답: 200 OK
    // 	2. 유효하지 않은 USER-TOKEN:
    // 	• 헤더: USER-TOKEN: invalid-token
    // 	• 응답: 400 Bad Request, "Invalid or missing USER-TOKEN header".
    @GetMapping("")
    fun getConcerts(
        @RequestHeader("USER-TOKEN") userToken: String?,
    ): ResponseEntity<*> {
        return try {
            if (userToken.isNullOrBlank() || userToken !in validTokens) {
                throw IllegalArgumentException("Invalid or missing USER-TOKEN")
            }
            val response =
                ResponseEntity.ok(
                    ApiResponse(
                        success = true,
                        code = "SUCCESS_01",
                        message = "Success",
                        data = concerts,
                    ),
                )
            ResponseEntity.ok(response)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse(
                    code = "FAIL_01",
                    message = "Request is invalid",
                    data = ex.message,
                ),
            )
        }
    }

    // 특정 콘서트 조회 API
    // 1. 정상 응답:
    //  • 경로: /api/concerts/1
    // 	• 헤더: USER-TOKEN: Bearer valid-token-1
    //  • 응답: 200 OK
    // 	2.	유효하지 않은 USER-TOKEN:
    // 	•	헤더: USER-TOKEN: invalid-token
    // 	•	응답: 400 Bad Request, "Invalid or missing USER-TOKEN header".
    // 3. 존재하지 않는 concertId:
    //  • 경로: /api/concerts/999
    // 	• 헤더: USER-TOKEN: Bearer valid-token-1
    //  • 응답: 404 Not Found
    @GetMapping("/{concertId}")
    fun getConcertById(
        @RequestHeader("USER-TOKEN") userToken: String?,
        @PathVariable concertId: Long,
    ): ResponseEntity<*> {
        return try {
            if (userToken.isNullOrBlank() || userToken !in validTokens) {
                throw IllegalArgumentException("Invalid or missing USER-TOKEN")
            }

            val concertResponse = concerts.find { it.concertId == concertId }
            if (concertResponse == null) {
                throw NoSuchElementException("Concert not found")
            }

            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    code = "SUCCESS_01",
                    message = "Success",
                    data = concertResponse,
                ),
            )
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    code = "INVALID_TOKEN",
                    message = ex.message ?: "Invalid request",
                    data = null,
                ),
            )
        } catch (ex: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(
                    code = "CONCERT_ERROR_01",
                    message = "Concert not found",
                    data = ex.message,
                ),
            )
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse(
                    code = "CONCERT_ERROR_02",
                    message = "fetch concert failed",
                    data = ex.message,
                ),
            )
        }
    }

    /**
     * 콘서트 스케줄 조회 API
     * 1. 정상 응답:
     *    - 경로: /api/concert/1/schedules
     *    - 응답: 200 OK
     * 2. 유효하지 않은 concertId:
     *    - 경로: /api/concert/999/schedules
     *    - 응답: 404 Not Found
     */
    @GetMapping("/{concertId}/schedules")
    fun getConcertSchedules(
        @RequestHeader("USER-TOKEN") userToken: String?,
        @PathVariable concertId: Long,
    ): ResponseEntity<*> {
        return try {
            // USER-TOKEN 검증
            if (userToken.isNullOrBlank() || userToken !in validTokens) {
                throw IllegalArgumentException("Invalid or missing USER-TOKEN header")
            }

            // 콘서트 ID로 콘서트 조회
            val concertResponse =
                concertSchedules.find { it.concertId == concertId }
                    ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponse(
                            success = false,
                            code = "CONCERT_NOT_FOUND",
                            message = "Concert not found",
                            data = null,
                        ),
                    )

            // 성공 응답
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    code = "SUCCESS_01",
                    message = "Success",
                    data = concertResponse,
                ),
            )
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    code = "INVALID_TOKEN",
                    message = ex.message ?: "Invalid request",
                    data = null,
                ),
            )
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse(
                    success = false,
                    code = "SERVER_ERROR",
                    message = "An unexpected error occurred",
                    data = null,
                ),
            )
        }
    }

    // 예약 가능 날짜/좌석 조회 API
// 	1.	정상응답:
// 	•	경로: /api/concert/1/schedules/101/seats?date=2025-01-01
// 	•	헤더: USER-TOKEN: Bearer valid-token-1
// 	•	응답: 200 OK, available: true, seats: [1, 2, 3].
// 	2.	유효하지 않은 USER-TOKEN:
// 	•	헤더: USER-TOKEN: invalid-token
// 	•	응답: 400 Bad Request, "Invalid or missing USER-TOKEN header".
// 	3.	유효하지 않은 concertId 또는 scheduleId:
// 	•	경로: /api/concert/999/schedules/888/seats?date=2025-01-01
// 	•	응답: 400 Bad Request, "Invalid concertId or scheduleId".
// 	4.	좌석이 없는 경우:
// 	•	경로: /api/concert/1/schedules/101/seats?date=2025-01-02
// 	•	응답: 200 OK, available: false, seats: [].
    @GetMapping("/{concertId}/schedules/{scheduleId}/seats")
    fun getAvailableSeats(
        @PathVariable concertId: Long,
        @PathVariable scheduleId: Long,
        @RequestParam date: String,
        @RequestHeader("USER-TOKEN") userToken: String?,
    ): ResponseEntity<*> {
        return try {
            if (userToken.isNullOrBlank() || userToken !in validTokens) {
                throw IllegalArgumentException("Invalid or missing USER-TOKEN")
            }
            val schedule =
                validConcertSchedules[concertId]?.get(scheduleId)
                    ?: throw IllegalArgumentException("Invalid concertId or scheduleId")
            val seats = schedule[date] ?: emptyList()

            val seatInfo = SeatInfoResponse(available = seats.isNotEmpty(), seats = seats)
            val response =
                ApiResponse(
                    success = true,
                    code = "SUCCESS_01",
                    message = "Success",
                    data = seatInfo,
                )
            ResponseEntity.ok(response)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse(
                    code = "FAIL_01",
                    message = "Request is invalid",
                    data = ex.message,
                ),
            )
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse(
                    code = "SEAT_ERROR_01",
                    message = "SeatError",
                    data = "fetch concert seat is occurred error",
                ),
            )
        }
    }
}

data class ConcertResponse(
    val concertId: Long,
    val title: String,
)

data class ConcertWithScheduleResponse(
    val concertId: Long,
    val title: String,
    val schedules: List<ConcertScheduleResponse>,
)

data class ConcertScheduleResponse(
    val scheduleId: Long,
    val performanceDate: String,
    val startTime: String,
    val endTime: String,
    val place: PlaceInfo,
)

data class PlaceInfo(
    val name: String,
    val availableSeatCount: Int,
)

data class SeatInfoResponse(
    val available: Boolean,
    val seats: List<Int>,
)

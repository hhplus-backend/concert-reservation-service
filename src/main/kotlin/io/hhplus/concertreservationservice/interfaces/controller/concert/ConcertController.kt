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

    private val validTokens = setOf("Bearer valid-token-1", "Bearer valid-token-2")

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

data class SeatInfoResponse(
    val available: Boolean,
    val seats: List<Int>,
)

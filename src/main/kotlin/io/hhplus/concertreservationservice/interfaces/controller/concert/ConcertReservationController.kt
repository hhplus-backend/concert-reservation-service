package io.hhplus.concertreservationservice.interfaces.controller.concert

import io.hhplus.concertreservationservice.interfaces.response.ApiResponse
import io.hhplus.concertreservationservice.interfaces.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/concert")
class ConcertReservationController {
    private val validConcertSchedules =
        mapOf(
            1L to
                mapOf(
                    101L to
                        mapOf(
                            1L to mutableListOf(1, 2, 3),
                            2L to mutableListOf(4, 5),
                        ),
                ),
        )

    private val validTokens = setOf("Bearer valid-token-1", "Bearer valid-token-2")

    // 좌석 예약 요청 API
    // 	1.	정상응답:
    // 	•	경로: /api/concert/1/schedules/101/reservations/1
    // 	•	헤더: USER-TOKEN: Bearer valid-token-1
    // 	•	요청: { "reservationId": 1 }
    // 	•	응답: 200 OK, reservationId:1, seatNo: 1
    // 	2.	유효하지 않은 USER-TOKEN:
    // 	•	헤더: USER-TOKEN: invalid-token
    // 	•	응답: 400 Bad Request, "Invalid or missing USER-TOKEN header".
    // 	3.	유효하지 않은 concertId 또는 scheduleId:
    // 	•	경로: /api/concert/999/schedules/888/reservations
    // 	•	응답: 400 Bad Request, "Invalid concertId or scheduleId".
    // 	3.	유효하지 않은 좌석 번호(50 이상의 수)
    // 	•	요청: { "reservationId": 51 }
    // 	•	응답: 400 Bad Request, "Invalid seatNo".
    @PostMapping("/{concertId}/schedules/{scheduleId}/reservations/{reservationId}")
    fun reserveSeat(
        @PathVariable concertId: Long,
        @PathVariable scheduleId: Long,
        @PathVariable reservationId: Long,
        @RequestBody reservationRequest: ReservationRequest,
        @RequestHeader("USER-TOKEN") userToken: String,
    ): ResponseEntity<*> {
        return try {
            if (userToken.isNullOrBlank() || userToken !in validTokens) {
                throw IllegalArgumentException("Invalid or missing USER-TOKEN")
            }

            val schedule =
                validConcertSchedules[concertId]?.get(scheduleId)
                    ?: throw IllegalArgumentException("Invalid concertId or scheduleId")

            if (reservationRequest.seatNo > 50) {
                throw IllegalArgumentException("Invalid seatNo")
            }

            val seatNo = reservationRequest.seatNo
            val seats = schedule[reservationId]!!

            if (!seats.contains(seatNo)) {
                throw IllegalArgumentException("Invalid seatNo")
            }

            val reservationResponse = ReservationResponse(reservationId = 1L, seatNo = seatNo)
            val response =
                ApiResponse(
                    success = true,
                    code = "SUCCESS_01",
                    message = "Success",
                    data = reservationResponse,
                )
            ResponseEntity.status(HttpStatus.CREATED).body(response)
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
                    code = "RESERVATION_ERROR_01",
                    message = "ReservationError",
                    data = "reservation failed",
                ),
            )
        }
    }
}

data class ReservationRequest(
    val seatNo: Int,
)

data class ReservationResponse(
    val reservationId: Long,
    val seatNo: Int,
)

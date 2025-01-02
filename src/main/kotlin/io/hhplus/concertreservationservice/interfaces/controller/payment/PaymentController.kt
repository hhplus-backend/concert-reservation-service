package io.hhplus.concertreservationservice.interfaces.controller.payment

import io.hhplus.concertreservationservice.interfaces.response.ApiResponse
import io.hhplus.concertreservationservice.interfaces.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payment")
class PaymentController {
    // 결제 요청 API
    // 1.	결제 성공:
    // 	•	헤더: USER-TOKEN: Bearer valid-token-1
    // 	•	요청: { "reservationId": 100 }
    // 	2.	결제 실패:
    // 	•	헤더: USER-TOKEN: Bearer valid-token-2
    // 	•	요청: { "reservationId": 100 }
    // 	3.	유효하지 않은 예약 ID:
    // 	•	헤더: USER-TOKEN: Bearer valid-token-1
    // 	•	요청: { "reservationId": 200 }
    // 	4.	유효하지 않은 USER-TOKEN:
    // 	•	헤더: USER-TOKEN: invalid-token
    // 	•	요청: { "reservationId": 100 }
    // 	5.	예약 ID 누락 또는 음수:
    // 	•	헤더: USER-TOKEN: Bearer valid-token-1
    // 	•	요청: { "reservationId": -1 }
    @PostMapping("")
    fun makePayment(
        @RequestHeader("USER-TOKEN") userToken: String?,
        @RequestBody paymentRequest: PaymentRequest?,
    ): ResponseEntity<*> {
        return try {
            if (userToken.isNullOrBlank() || !userToken.startsWith("Bearer ")) {
                throw IllegalArgumentException("Invalid or missing USER-TOKEN header")
            }
            if (paymentRequest == null || paymentRequest.reservationId <= 0) {
                throw IllegalArgumentException("Invalid payment request data")
            }

            val response =
                when {
                    userToken == "Bearer valid-token-1" && paymentRequest.reservationId == 100L -> {
                        val paymentResultResponse = PaymentResultResponse(status = "SUCCESS")
                        ApiResponse(
                            success = true,
                            code = "SUCCESS_01",
                            message = "Success",
                            data = paymentResultResponse,
                        )
                    }

                    userToken == "Bearer valid-token-2" -> {
                        throw IllegalArgumentException("Insufficient funds for payment")
                    }

                    paymentRequest.reservationId == 200L -> {
                        throw IllegalArgumentException("Invalid reservation ID")
                    }

                    else -> {
                        throw Exception("payment is failed")
                    }
                }

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
                    code = "PAYMENT_ERROR_01",
                    message = "PaymentError",
                    data = ex.message,
                ),
            )
        }
    }
}

data class PaymentRequest(
    val reservationId: Long,
)

data class PaymentResultResponse(
    val status: String,
)

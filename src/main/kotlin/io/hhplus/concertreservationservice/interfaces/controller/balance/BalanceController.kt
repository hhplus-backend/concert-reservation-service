package io.hhplus.concertreservationservice.interfaces.controller.balance

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
@RequestMapping("/api/balance")
class BalanceController {
    private val userBalances =
        mutableMapOf(
            "Bearer valid-token-1" to 1000L,
            "Bearer valid-token-2" to 500L,
        )

    // 잔액 충전 API
    // 	1.	충전 성공:
    // 	•	헤더: USER-TOKEN: Bearer valid-token-1
    // 	•	요청: { "amount": 500 }
    // 	•	응답: 200 OK, 새로운 잔액: 1500.
    // 	2.	유효하지 않은 USER-TOKEN:
    // 	•	헤더: USER-TOKEN: invalid-token
    // 	•	요청: { "amount": 500 }
    // 	•	응답: 400 Bad Request, “Invalid or missing USER-TOKEN header”.
    // 	3.	유효하지 않은 금액:
    // 	•	헤더: USER-TOKEN: Bearer valid-token-1
    // 	•	요청: { "amount": -100 }
    // 	•	응답: 400 Bad Request, “Invalid balance request data”.
    @PostMapping("")
    fun chargeBalance(
        @RequestHeader("USER-TOKEN") userToken: String?,
        @RequestBody balanceRequest: BalanceChargeRequest?,
    ): ResponseEntity<*> {
        return try {
            if (userToken.isNullOrBlank() || !userBalances.containsKey(userToken)) {
                throw IllegalArgumentException("Invalid or missing USER-TOKEN header")
            }
            if (balanceRequest == null || balanceRequest.amount <= 0) {
                throw IllegalArgumentException("Invalid balance request data")
            }

            val newBalance =
                userBalances.computeIfPresent(userToken) { _, currentBalance ->
                    currentBalance + balanceRequest.amount
                } ?: throw IllegalStateException("User balance not found")

            val response =
                ApiResponse(
                    success = true,
                    code = "SUCCESS_01",
                    message = "Success",
                    data = BalanceResponse(amount = newBalance),
                )
            ResponseEntity.ok(response)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse(
                    code = "ERROR_01",
                    message = "Request is invalid",
                    data = ex.message,
                ),
            )
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse(
                    code = "BALANCE_ERROR_02",
                    message = "BalanceChargeError",
                    data = "balance charge is failed",
                ),
            )
        }
    }

    // 잔액 조회 API
    //  1.	Happy Case:
    // 	•	헤더: USER-TOKEN: Bearer valid-token-2
    // 	•	응답: 200 OK, 현재 잔액: 500.
    // 	2.	유효하지 않은 USER-TOKEN:
    // 	•	헤더: USER-TOKEN: invalid-token
    // 	•	응답: 400 Bad Request, “Invalid or missing USER-TOKEN header”.
    @GetMapping("")
    fun getBalance(
        @RequestHeader("USER-TOKEN") userToken: String?,
    ): ResponseEntity<*> {
        return try {
            if (userToken.isNullOrBlank() || !userBalances.containsKey(userToken)) {
                throw IllegalArgumentException("Invalid or missing USER-TOKEN header")
            }

            val currentBalance = userBalances[userToken] ?: 0L
            val response =
                ApiResponse(
                    success = true,
                    code = "SUCCESS_01",
                    message = "Success",
                    data = BalanceResponse(amount = currentBalance),
                )
            ResponseEntity.ok(response)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse(
                    code = "ERROR_01",
                    message = "Request is invalid",
                    data = ex.message,
                ),
            )
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse(
                    code = "BALANCE_ERROR_01",
                    message = "BalanceError",
                    data = "fetch balance is failed",
                ),
            )
        }
    }
}

data class BalanceChargeRequest(
    val amount: Long,
)

data class BalanceResponse(
    val amount: Long,
)

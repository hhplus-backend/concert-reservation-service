package io.hhplus.concertreservationservice.interfaces.response

import java.time.LocalDateTime

// success가 없다면으로 에러를 판단 하기 위해 success 없음
data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: Any?,
)

package io.hhplus.concertreservationservice.interfaces.response

import java.time.LocalDateTime

// 공통 응답 포맷
data class ApiResponse(
    val success: Boolean,
    val code: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: Any?,
)

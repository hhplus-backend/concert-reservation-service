package io.hhplus.concertreservationservice.domain.token.repository

import io.hhplus.concertreservationservice.application.service.token.request.TokenStatusCommand
import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.Optional

interface ReservationTokenRepository {
    fun saveToken(token: ReservationToken): ReservationToken

    fun getToken(command: TokenStatusCommand): Optional<ReservationToken>

    fun getExpiredToken(currentTime: LocalDateTime): List<ReservationToken>

    fun deleteTokens(tokens: List<ReservationToken>)

    fun deleteToken(tokens: ReservationToken)

    fun deleteTokenByName(token: String)

    fun getActiveTokenCount(status: TokenStatus): Int

    fun getWaitingTokensForActivation(
        status: TokenStatus,
        pageable: Pageable,
    ): List<ReservationToken>

    fun updateTokenStatus(
        ids: List<Long>,
        newStatus: TokenStatus,
    ): Int
}
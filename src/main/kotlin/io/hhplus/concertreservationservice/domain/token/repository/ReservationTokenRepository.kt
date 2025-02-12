package io.hhplus.concertreservationservice.domain.token.repository

import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.TokenStatus
import io.hhplus.concertreservationservice.domain.token.service.request.TokenStatusCommand
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface ReservationTokenRepository {
    fun saveToken(token: ReservationToken): ReservationToken

    fun findToken(command: TokenStatusCommand): ReservationToken?

    fun findRankWaitingToken(command: TokenStatusCommand): Long?

    fun removeExpiredActiveTokens(currentTime: LocalDateTime): Int

    fun deleteTokens(tokens: List<ReservationToken>)

    fun deleteToken(tokens: ReservationToken)

    fun deleteTokenByName(token: String)

    fun getActiveTokenCount(status: TokenStatus): Int

    fun getWaitingTokensForActivation(pageable: Pageable): List<ReservationToken>

    fun updateToActiveStatus(
        tokens: List<ReservationToken>,
        currentTime: LocalDateTime,
    ): Int
}

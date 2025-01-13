package io.hhplus.concertreservationservice.domain.token.service

import io.hhplus.concertreservationservice.domain.token.ReservationToken
import io.hhplus.concertreservationservice.domain.token.exception.TokenNotFoundException
import io.hhplus.concertreservationservice.domain.token.extension.toCreateTokenInfo
import io.hhplus.concertreservationservice.domain.token.extension.toReservationToken
import io.hhplus.concertreservationservice.domain.token.repository.ReservationTokenRepository
import io.hhplus.concertreservationservice.domain.token.service.request.CreateTokenCommand
import io.hhplus.concertreservationservice.domain.token.service.request.TokenStatusCommand
import io.hhplus.concertreservationservice.domain.token.service.response.CreateTokenInfo
import io.hhplus.concertreservationservice.domain.token.service.response.TokenStatusInfo
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TokenService(
    private val reservationTokenRepository: ReservationTokenRepository,
) {
    fun saveToken(command: CreateTokenCommand): CreateTokenInfo {
        val token =
            ReservationToken(
                token = command.token,
                expiredAt = LocalDateTime.now().plusMinutes(5),
                userId = command.userId,
            )

        val reservationToken = reservationTokenRepository.saveToken(token)
        return reservationToken.toCreateTokenInfo()
    }

    fun getToken(command: TokenStatusCommand): TokenStatusInfo {
        val reservationToken =
            reservationTokenRepository.getToken(command) ?: throw TokenNotFoundException(command.token)

        return reservationToken.toReservationToken()
    }

    fun deleteToken(token: String) {
        reservationTokenRepository.deleteTokenByName(token)
    }
}
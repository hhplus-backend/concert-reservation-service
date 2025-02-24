package io.hhplus.concertreservationservice.application.facade.token

import io.hhplus.concertreservationservice.application.facade.token.request.CreateReservationTokenCriteria
import io.hhplus.concertreservationservice.application.facade.token.request.ReservationTokenStatusCriteria
import io.hhplus.concertreservationservice.application.facade.token.response.CreateReservationTokenResult
import io.hhplus.concertreservationservice.application.facade.token.response.ReservationTokenPollingResult
import io.hhplus.concertreservationservice.application.helper.TokenProvider
import io.hhplus.concertreservationservice.domain.token.service.TokenService
import io.hhplus.concertreservationservice.domain.token.service.request.CreateTokenCommand
import io.hhplus.concertreservationservice.domain.token.service.request.TokenStatusCommand
import io.hhplus.concertreservationservice.domain.token.service.response.toCreateReservationTokenResult
import io.hhplus.concertreservationservice.domain.token.service.response.toResult
import io.hhplus.concertreservationservice.domain.user.service.UserService
import org.springframework.stereotype.Component

@Component
class TokenFacade(
    private val jwtTokenProvider: TokenProvider,
    private val tokenService: TokenService,
    private val userService: UserService,
) {
    fun createToken(criteria: CreateReservationTokenCriteria): CreateReservationTokenResult {
        val user = userService.getUser(criteria.userId)
        val token = jwtTokenProvider.generateToken(criteria.userId)
        val tokenInfo = tokenService.saveToken(CreateTokenCommand(token, criteria.userId))
        return tokenInfo.toCreateReservationTokenResult()
    }

    fun getTokenStatus(criteria: ReservationTokenStatusCriteria): ReservationTokenPollingResult {
        val tokenInfo = tokenService.getRank(TokenStatusCommand(criteria.token))
        return tokenInfo.toResult()
    }
}

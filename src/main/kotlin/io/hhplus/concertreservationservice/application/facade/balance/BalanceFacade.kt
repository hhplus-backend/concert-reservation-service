package io.hhplus.concertreservationservice.application.facade.balance

import io.hhplus.concertreservationservice.application.facade.balance.request.ChargeBalanceCriteria
import io.hhplus.concertreservationservice.application.facade.balance.request.FetchBalanceCriteria
import io.hhplus.concertreservationservice.application.facade.balance.response.ChargeBalanceResult
import io.hhplus.concertreservationservice.application.facade.balance.response.FetchBalanceResult
import io.hhplus.concertreservationservice.application.service.balance.BalanceService
import io.hhplus.concertreservationservice.application.service.balance.request.ChargeBalanceCommand
import io.hhplus.concertreservationservice.application.service.balance.request.FetchBalanceCommand
import io.hhplus.concertreservationservice.application.service.balance.response.toChargeBalanceResult
import io.hhplus.concertreservationservice.application.service.balance.response.toFetchBalanceResult
import io.hhplus.concertreservationservice.application.service.token.TokenService
import io.hhplus.concertreservationservice.application.service.token.request.TokenStatusCommand
import org.springframework.stereotype.Component

@Component
class BalanceFacade(
    private val balanceService: BalanceService,
    private val tokenService: TokenService,
) {
    fun chargeBalance(criteria: ChargeBalanceCriteria): ChargeBalanceResult {
        val tokenInfo = tokenService.getToken(TokenStatusCommand(criteria.token))
        val chargedBalanceInfo = balanceService.chargeBalance(ChargeBalanceCommand(tokenInfo.userId, criteria.amount))
        return chargedBalanceInfo.toChargeBalanceResult()
    }

    fun getBalance(criteria: FetchBalanceCriteria): FetchBalanceResult {
        val tokenInfo = tokenService.getToken(TokenStatusCommand(criteria.token))
        val fetchBalanceInfo = balanceService.getBalance(FetchBalanceCommand(tokenInfo.userId))
        return fetchBalanceInfo.toFetchBalanceResult()
    }
}

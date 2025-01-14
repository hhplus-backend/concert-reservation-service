package io.hhplus.concertreservationservice.domain.concert.service

import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.exception.SeatNotFoundException
import io.hhplus.concertreservationservice.domain.concert.extensions.toSeatInfo
import io.hhplus.concertreservationservice.domain.concert.repository.SeatRepository
import io.hhplus.concertreservationservice.domain.concert.service.request.ReserveSeatCommand
import io.hhplus.concertreservationservice.domain.concert.service.request.SearchAvailSeatCommand
import io.hhplus.concertreservationservice.domain.concert.service.response.SeatInfo
import org.springframework.stereotype.Service

@Service
class ConcertService(
    private val seatRepository: SeatRepository,
) {
    fun getAvailableSeats(command: SearchAvailSeatCommand): List<SeatInfo> {
        val seats = seatRepository.getAvailableSeats(command)
        return seats.map { it.toSeatInfo() }
    }

    fun getSeatForReservationWithLock(command: ReserveSeatCommand): Seat {
        return seatRepository.getSeatForReservationWithLock(command) ?: throw SeatNotFoundException()
    }
}

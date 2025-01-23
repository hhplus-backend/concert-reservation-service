package io.hhplus.concertreservationservice.domain.concert.service

import io.hhplus.concertreservationservice.domain.DateRange
import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.domain.concert.Concert
import io.hhplus.concertreservationservice.domain.concert.Place
import io.hhplus.concertreservationservice.domain.concert.Schedule
import io.hhplus.concertreservationservice.domain.concert.ScheduleSeat
import io.hhplus.concertreservationservice.domain.concert.Seat
import io.hhplus.concertreservationservice.domain.concert.SeatType
import io.hhplus.concertreservationservice.domain.concert.service.request.CreateReserveSeatCommand
import io.hhplus.concertreservationservice.domain.reservation.service.ReservationService
import io.hhplus.concertreservationservice.domain.user.User
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ConcertJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.PlaceJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.ScheduleSeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.SeatReservationJpaRepository
import io.hhplus.concertreservationservice.infrastructure.persistence.jpa.UserJpaRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ActiveProfiles("integration-test")
class ReservationConcurrencyServiceTest(
    private val seatReservationJpaRepository: SeatReservationJpaRepository,
    private val seatJpaRepository: SeatJpaRepository,
    private val placeJpaRepository: PlaceJpaRepository,
    private val concertJpaRepository: ConcertJpaRepository,
    private val scheduleSeatJpaRepository: ScheduleSeatJpaRepository,
    private val scheduleJpaRepository: ScheduleJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val reservationService: ReservationService,
) : BehaviorSpec({

        afterEach {
            seatReservationJpaRepository.deleteAllInBatch()
            seatJpaRepository.deleteAllInBatch()
            scheduleSeatJpaRepository.deleteAllInBatch()
            scheduleJpaRepository.deleteAllInBatch()
            concertJpaRepository.deleteAllInBatch()
            placeJpaRepository.deleteAllInBatch()
            userJpaRepository.deleteAllInBatch()
        }

        given("두 개의 동시 예약 요청이 있을 때") {
            val user =
                userJpaRepository.save(
                    User(
                        name = "길길",
                    ),
                )
            val seatNo = 1

            val place = placeJpaRepository.save(Place(name = "상암월드컵경기장", availableSeatCount = 1000))
            val concert = concertJpaRepository.save(Concert(title = "싸이 흠뻑쇼"))
            val schedule =
                scheduleJpaRepository.save(
                    Schedule(
                        performanceDate = LocalDate.of(2024, 1, 1),
                        performanceTime = 300,
                        reservationPeriod =
                            DateRange(
                                start = LocalDate.of(2023, 12, 1),
                                end = LocalDate.of(2023, 12, 31),
                            ),
                        concert = concert,
                        place = place,
                    ),
                )

            val scheduleSeat =
                scheduleSeatJpaRepository.save(
                    ScheduleSeat(
                        type = SeatType.UNDEFINED,
                        price = Money(50000),
                        seatCount = 50,
                        schedule = schedule,
                    ),
                )

            // 예약을 시작할 수 있도록 설정
            val seat = seatJpaRepository.save(Seat(no = seatNo, scheduleSeat = scheduleSeat))

            val command =
                CreateReserveSeatCommand(
                    concertId = seat.scheduleSeat.schedule.concert.id,
                    scheduleId = seat.scheduleSeat.schedule.id,
                    user = user,
                    seatNo = seatNo,
                )

            `when`("20개 스레드가 동시에 예약을 시도하면") {
                val threadCount = 20
                val latch = CountDownLatch(threadCount)
                val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
                val successfulApplications = AtomicInteger(0)
                val failApplications = AtomicInteger(0)

                for (i in 1..threadCount) {
                    executor.submit {
                        try {
                            reservationService.createReservationInfo(command)
                            successfulApplications.incrementAndGet()
                        } catch (e: Exception) {
                            failApplications.incrementAndGet()
                        } finally {
                            latch.countDown()
                        }
                    }
                }

                latch.await()
                executor.shutdown()

                then("예약이 한 번만 완료되어야 한다") {
                    successfulApplications.get() shouldBe 1
                }
            }

            `when`("50개 스레드가 동시에 예약을 시도하면") {
                val threadCount = 50
                val latch = CountDownLatch(threadCount)
                val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
                val successfulApplications = AtomicInteger(0)
                val failApplications = AtomicInteger(0)

                for (i in 1..threadCount) {
                    executor.submit {
                        try {
                            reservationService.createReservationInfo(command)
                            successfulApplications.incrementAndGet()
                        } catch (e: Exception) {
                            failApplications.incrementAndGet()
                        } finally {
                            latch.countDown()
                        }
                    }
                }

                latch.await()
                executor.shutdown()

                then("예약이 한 번만 완료되어야 한다") {
                    successfulApplications.get() shouldBe 1
                }
            }

            `when`("1000개 스레드가 동시에 예약을 시도하면") {
                val threadCount = 1000
                val latch = CountDownLatch(threadCount)
                val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
                val successfulApplications = AtomicInteger(0)
                val failApplications = AtomicInteger(0)

                for (i in 1..threadCount) {
                    executor.submit {
                        try {
                            reservationService.createReservationInfo(command)
                            successfulApplications.incrementAndGet()
                        } catch (e: Exception) {
                            failApplications.incrementAndGet()
                        } finally {
                            latch.countDown()
                        }
                    }
                }

                latch.await()
                executor.shutdown()

                then("예약이 한 번만 완료되어야 한다") {
                    successfulApplications.get() shouldBe 1
                }
            }

            `when`("3000개 스레드가 동시에 예약을 시도하면") {
                val threadCount = 3000
                val latch = CountDownLatch(threadCount)
                val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
                val successfulApplications = AtomicInteger(0)
                val failApplications = AtomicInteger(0)

                for (i in 1..threadCount) {
                    executor.submit {
                        try {
                            reservationService.createReservationInfo(command)
                            successfulApplications.incrementAndGet()
                        } catch (e: Exception) {
                            failApplications.incrementAndGet()
                        } finally {
                            latch.countDown()
                        }
                    }
                }

                latch.await()
                executor.shutdown()

                then("예약이 한 번만 완료되어야 한다") {
                    successfulApplications.get() shouldBe 1
                }
            }
        }
    })

package io.hhplus.concertreservationservice.domain.payment.event.publisher

import io.hhplus.concertreservationservice.domain.payment.event.PaymentCompletedEvent

interface PaymentEventPublisher {
    fun publishCompletedEvent(event: PaymentCompletedEvent)
}

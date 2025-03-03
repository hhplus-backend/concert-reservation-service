package io.hhplus.concertreservationservice.domain.user

import io.hhplus.concertreservationservice.domain.balance.Money
import io.hhplus.concertreservationservice.infrastructure.persistence.BaseEntity
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.Comment

@Entity
@Comment("사용자")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    val id: Long = 0L,
    @Column(nullable = false)
    @Comment("사용자 이름")
    val name: String,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "amount", column = Column(name = "balance")),
    )
    var balance: Money = Money(0),
) : BaseEntity() {
    fun chargeMoney(amount: Money) {
        require(amount.amount > 0) { "충전 금액은 0원 이상이어야 합니다." }
        this.balance = this.balance.add(amount)
    }

    fun deductMoney(amount: Money) {
        require(amount.amount > 0) { "사용 금액은 0원 이상이어야 합니다." }
        this.balance = this.balance.subtract(amount)
    }
}

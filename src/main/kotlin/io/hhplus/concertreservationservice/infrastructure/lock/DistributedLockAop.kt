package io.hhplus.concertreservationservice.infrastructure.lock

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class DistributedLockAop(
    private val redissonClient: RedissonClient,
    private val aopForTransaction: AopForTransaction,
) {
    companion object {
        private const val REDISSON_LOCK_PREFIX = "LOCK:"
        private val log: Logger = LoggerFactory.getLogger(DistributedLockAop::class.java)
    }

    @Around("@annotation(io.hhplus.concertreservationservice.infrastructure.lock.DistributedLockWithTransactional)")
    @Throws(Throwable::class)
    fun lock(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val distributedLockWithTransactional = method.getAnnotation(DistributedLockWithTransactional::class.java)

        val key =
            REDISSON_LOCK_PREFIX +
                CustomSpringELParser.getDynamicValue(
                    signature.parameterNames,
                    joinPoint.args,
                    distributedLockWithTransactional.key,
                )
        val rLock: RLock = redissonClient.getLock(key)

        return try {
            val available =
                rLock.tryLock(
                    distributedLockWithTransactional.waitTime,
                    distributedLockWithTransactional.leaseTime,
                    distributedLockWithTransactional.timeUnit,
                )
            if (!available) {
                return false
            }

            aopForTransaction.proceed(joinPoint)
        } catch (e: InterruptedException) {
            throw InterruptedException()
        } finally {
            try {
                if (rLock.isHeldByCurrentThread) {
                    rLock.unlock()
                }
            } catch (e: IllegalMonitorStateException) {
                log.info(
                    "Redisson Lock Already Unlocked: serviceName = {}, key = {}",
                    method.name,
                    key,
                )
            }
        }
    }
}

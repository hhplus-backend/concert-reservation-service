package io.hhplus.concertreservationservice.infrastructure

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

@PropertySource("classpath:jwt.yml")
@Component
class TokenProvider(
    @Value("\${secret-key}") secretKey: String,
    @Value("\${access-token-expiration}") private val accessTokenExpiration: Long,
) {
    private val key: Key

    init {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    // 토큰 생성
    fun generateToken(userId: Long): String {
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(key)
            .compact()
    }

    // 토큰 검증
    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        } catch (e: SecurityException) {
            throw JwtException("잘못된 JWT 서명입니다.")
        } catch (e: MalformedJwtException) {
            throw JwtException("잘못된 JWT 서명입니다.")
        } catch (e: ExpiredJwtException) {
            throw JwtException("만료된 JWT 토큰입니다.")
        } catch (e: UnsupportedJwtException) {
            throw UnsupportedJwtException("지원되지 않는 JWT 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("JWT 토큰이 잘못되었습니다.")
        }

        return true
    }
}

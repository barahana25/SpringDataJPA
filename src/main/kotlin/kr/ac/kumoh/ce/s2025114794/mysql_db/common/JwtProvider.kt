package kr.ac.kumoh.ce.s2025114794.mysql_db.common

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

/**
 * 간단·안전하게 JWT를 발급/검증하는 헬퍼.
 * - HS256 알고리즘 사용
 * - `jwt.secret`(Base64 또는 plain text) → 32바이트(256bit) 키로 변환
 * - 만료 시간 초는 `jwt.expiration` (기본 7200초 = 2시간)
 */
@Component
class JwtProvider(
    @Value("\${jwt.secret:}") rawSecret: String?,
    @Value("\${jwt.expiration:7200}") private val expSeconds: Long
) {
    private val key: SecretKey = buildKey(rawSecret)

    /** 토큰 생성 */
    fun generateToken(userId: String, role: String): String {
        val now = Date()
        return Jwts.builder()
            .setSubject(userId)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + expSeconds * 1000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    /** Claims 추출 (검증 포함) */
    fun extractAllClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

    /** 토큰 유효성만 확인 (만료·서명 오류 등) */
    fun validate(token: String): Boolean =
        runCatching { extractAllClaims(token); true }.getOrElse { false }

    // ------------------------------------------------------------------------
    // 내부 키 변환 로직
    // ------------------------------------------------------------------------
    private fun buildKey(secret: String?): SecretKey {
        val src = secret?.takeIf { it.isNotBlank() }
            ?: "THIS_IS_DEMO_SECRET_PLEASE_OVERRIDE".repeat(4)

        // Base64 ◇ Plain 문자열 모두 허용
        val bytes = runCatching { Decoders.BASE64.decode(src) }.getOrElse { src.toByteArray() }

        // 길이를 32바이트(256bit)로 보정 → 부족하면 zero-pad, 길면 자름
        val fixed = if (bytes.size >= 32) bytes.copyOf(32) else bytes + ByteArray(32 - bytes.size)
        return Keys.hmacShaKeyFor(fixed)
    }
}
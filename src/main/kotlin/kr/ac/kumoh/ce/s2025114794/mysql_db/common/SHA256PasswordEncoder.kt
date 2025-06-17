package kr.ac.kumoh.ce.s2025114794.mysql_db.common

import org.springframework.security.crypto.password.PasswordEncoder
import java.security.MessageDigest
import kotlin.experimental.and

class SHA256PasswordEncoder : PasswordEncoder {

    override fun encode(rawPassword: CharSequence): String =
        digest(rawPassword.toString())

    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean =
        digest(rawPassword.toString()) == encodedPassword

    /** 평문을 SHA-256 → 64자리 HEX 로 변환 */
    private fun digest(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b and 0xff.toByte()))
        }
        return sb.toString()
    }
}

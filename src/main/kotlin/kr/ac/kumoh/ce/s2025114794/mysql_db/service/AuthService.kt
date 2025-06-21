package kr.ac.kumoh.ce.s2025114794.mysql_db.service

import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.AuthDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.entity.LoginLog
import kr.ac.kumoh.ce.s2025114794.mysql_db.repository.*
import kr.ac.kumoh.ce.s2025114794.mysql_db.common.JwtProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 인증 관련 서비스.
 * - 학생 ID  : "S…"
 * - 교수 ID  : "P…"
 * - 관리자 ID: 그 외
 */
interface AuthService {
    /** 로그인 & JWT 발급 */
    fun login(req: AuthDto.LoginRequest): AuthDto.LoginResponse

    /** 비밀번호 변경 */
    fun changePassword(id: String, role: String, req: AuthDto.ChangePasswordRequest)
}

@Service
@Transactional
class AuthServiceImpl(
    private val studentAccRepo: StudentAccountRepository,
    private val professorAccRepo: ProfessorAccountRepository,
    private val adminAccRepo: AdminAccountRepository,
    private val logRepo: LoginLogRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider
) : AuthService {

    // -------------------------------------------------- 로그인
    override fun login(req: AuthDto.LoginRequest): AuthDto.LoginResponse {
        val (id, rawPw) = req

        // 1) 계정 조회 + 역할 판정
        val (storedPw, role) = when {
            id.startsWith("S") -> studentAccRepo.findById(id).orElseThrow() to "student"
            id.startsWith("P") -> professorAccRepo.findById(id).orElseThrow() to "professor"
            else -> adminAccRepo.findById(id).orElseThrow() to "admin"
        }.let { (acc, r) -> acc.password to r }

        // 2) 패스워드 검증
        require(passwordEncoder.matches(rawPw, storedPw)) { "Invalid credentials" }

        // 3) 토큰 발급 & 로그인 로그
        val token = jwtProvider.generateToken(id, role)
        logRepo.save(LoginLog(userId = id, role = role, lastLogin = LocalDateTime.now()))

        return AuthDto.LoginResponse(success = true, role = role, token = token)
    }

    // -------------------------------------------------- 비밀번호 변경
    override fun changePassword(id: String, role: String, req: AuthDto.ChangePasswordRequest) {
        val enc = passwordEncoder.encode(req.newPassword)
        when (role) {
            "student" -> studentAccRepo.findById(id).ifPresent { it.password = enc }
            "professor" -> professorAccRepo.findById(id).ifPresent { it.password = enc }
            else -> adminAccRepo.findById(id).ifPresent { it.password = enc }
        }
    }
}

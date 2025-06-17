package kr.ac.kumoh.ce.s2025114794.mysql_db.service

import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.AdminDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.entity.*
import kr.ac.kumoh.ce.s2025114794.mysql_db.repository.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 관리자 기능 서비스 – API 사양 / 테이블 정의를 그대로 반영.
 */
interface AdminService {
    /** 계정 생성 */
    fun createAccount(dto: AdminDto.AccountUpsert): Boolean

    /** 계정 수정 */
    fun updateAccount(id: String, dto: AdminDto.AccountUpsert): Boolean

    /** 계정 삭제 */
    fun deleteAccount(id: String): Boolean

    /** 강의 개설·수정(put) */
    fun upsertLecture(lecNo: String, dto: AdminDto.LectureUpsert): Boolean

    /** 강의 삭제 */
    fun deleteLecture(lecNo: String): Boolean

    /** 강의별 성적 통계 */
    fun getGradeStatistics(lecNo: String): AdminDto.GradeStatistics

    /** 로그인 로그 검색 */
    fun getLoginLogs(keyword: String): List<AdminDto.LoginLog>
}

@Service
@Transactional
class AdminServiceImpl(
    private val departmentRepo: DepartmentRepository,
    private val professorRepo: ProfessorRepository,
    private val studentRepo: StudentRepository,
    private val lectureRepo: LectureRepository,
    private val enrollmentRepo: EnrollmentRepository,

    private val studentAccRepo: StudentAccountRepository,
    private val professorAccRepo: ProfessorAccountRepository,
    private val adminAccRepo: AdminAccountRepository,

    private val logRepo: LoginLogRepository,
    private val encoder: PasswordEncoder
) : AdminService {

    // ---------------------------------------------------------------------
    // 계정 관리
    // ---------------------------------------------------------------------
    override fun createAccount(dto: AdminDto.AccountUpsert): Boolean {
        val enc = encoder.encode(dto.password)
        when (dto.role.lowercase()) {
            "student" -> studentAccRepo.save(StudentAccount(dto.id).apply { password = enc })
            "professor" -> professorAccRepo.save(ProfessorAccount(dto.id).apply { password = enc })
            "admin" -> adminAccRepo.save(AdminAccount(dto.id).apply { password = enc })
            else -> return false
        }
        return true
    }

    override fun updateAccount(id: String, dto: AdminDto.AccountUpsert): Boolean {
        val enc = encoder.encode(dto.password)
        return when (dto.role.lowercase()) {
            "student" -> studentAccRepo.findById(id).map { it.password = enc; true }.orElse(false)
            "professor" -> professorAccRepo.findById(id).map { it.password = enc; true }.orElse(false)
            "admin" -> adminAccRepo.findById(id).map { it.password = enc; true }.orElse(false)
            else -> false
        }
    }

    override fun deleteAccount(id: String): Boolean {
        val existed = studentAccRepo.existsById(id) || professorAccRepo.existsById(id) || adminAccRepo.existsById(id)
        studentAccRepo.deleteById(id)
        professorAccRepo.deleteById(id)
        adminAccRepo.deleteById(id)
        return existed
    }

    // ---------------------------------------------------------------------
    // 강의 관리
    // ---------------------------------------------------------------------
    override fun upsertLecture(lecNo: String, dto: AdminDto.LectureUpsert): Boolean {
        val dept = departmentRepo.findById(dto.deptId).orElseThrow { IllegalArgumentException("학과 없음: ${dto.deptId}") }
        val professor = professorRepo.findById(dto.pno).orElseThrow { IllegalArgumentException("교수 없음: ${dto.pno}") }

        lectureRepo.save(
            Lecture(
                lecNo = lecNo,
                lecName = dto.lecName,
                credit = dto.credit,
                lecTimeRoom = dto.lecTimeRoom,
                department = dept,
                professor = professor
            )
        )
        return true
    }

    override fun deleteLecture(lecNo: String): Boolean {
        val exists = lectureRepo.existsById(lecNo)
        lectureRepo.deleteById(lecNo)
        return exists
    }

    // ---------------------------------------------------------------------
    // 성적 통계
    // ---------------------------------------------------------------------
    @Transactional(readOnly = true)
    override fun getGradeStatistics(lecNo: String): AdminDto.GradeStatistics {
        val list = enrollmentRepo.findByLecture_LecNo(lecNo)
        if (list.isEmpty()) return AdminDto.GradeStatistics(0.0, 0.0, 0.0, emptyMap())

        val midAvg = list.mapNotNull { it.midScore }.average()
        val finAvg = list.mapNotNull { it.finalScore }.average()
        val totalAvg = (midAvg + finAvg) / 2
        val dist = list.groupingBy { it.grade ?: "F" }.eachCount()
        return AdminDto.GradeStatistics(totalAvg, midAvg, finAvg, dist)
    }

    // ---------------------------------------------------------------------
    // 로그인 로그
    // ---------------------------------------------------------------------
    @Transactional(readOnly = true)
    override fun getLoginLogs(keyword: String) = logRepo.findByUserIdContaining(keyword).map {
        AdminDto.LoginLog(it.userId, it.role, it.lastLogin.toString())
    }
}

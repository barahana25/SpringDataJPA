package kr.ac.kumoh.ce.s2025114794.mysql_db.service

import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.StudentDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.repository.EnrollmentRepository
import kr.ac.kumoh.ce.s2025114794.mysql_db.repository.StudentRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 학생 전용 비즈니스 로직
 */
interface StudentService {
    /** 학적 정보 */
    fun getProfile(sno: String): StudentDto.Profile

    /** 시간표 (연도·학기 필터 – 테이블에 연·학기 컬럼 없으므로 현재는 sno 기준만) */
    fun getTimetable(sno: String, year: Int, semester: Int): List<StudentDto.TimetableEntry>

    /** 개인 성적표 */
    fun getGrades(sno: String): StudentDto.GradeReport
}

@Service
@Transactional(readOnly = true)
class StudentServiceImpl(
    private val studentRepo: StudentRepository,
    private val enrollmentRepo: EnrollmentRepository
) : StudentService {

    // ----------------------------- 학적
    override fun getProfile(sno: String) = studentRepo.findById(sno).orElseThrow().let {
        StudentDto.Profile(
            sno = it.sno,
            sname = it.sname,
            gender = it.gender,
            deptName = it.department?.deptName ?: "",
            year = it.year ?: 1,
            phone = it.phone
        )
    }

    // ----------------------------- 시간표
    override fun getTimetable(sno: String, year: Int, semester: Int): List<StudentDto.TimetableEntry> {
        // 연·학기 컬럼이 없어 현재는 전체 반환. 추후 lecture 테이블 확장 시 필터 추가
        return enrollmentRepo.findByStudent_Sno(sno).map {
            val lec = it.lecture!!
            val (time, room) = splitTimeRoom(lec.lecTimeRoom)
            StudentDto.TimetableEntry(lec.lecNo, lec.lecName, time, room)
        }
    }

    // ----------------------------- 성적표
    override fun getGrades(sno: String): StudentDto.GradeReport {
        val details = enrollmentRepo.findByStudent_Sno(sno).mapNotNull { e ->
            e.grade?.let { g ->
                StudentDto.GradeDetail(
                    lecNo = e.lecture!!.lecNo,
                    lecName = e.lecture!!.lecName,
                    credit = e.lecture!!.credit,
                    grade = g,
                    score = gradeToScore(g)
                )
            }
        }
        val totalCredits = details.sumOf { it.credit }
        val weighted = details.sumOf { it.score.toDouble() * it.credit }
        val gpa = if (totalCredits == 0) 0f else (weighted / totalCredits).toFloat()
        return StudentDto.GradeReport(details.size, totalCredits, String.format("%.2f", gpa).toFloat(), details)
    }

    // ----------------------------- 헬퍼
    private fun splitTimeRoom(raw: String): Pair<String, String> =
        if (raw.contains(",")) raw.split(",").first() to raw.split(",").last() else raw to ""

    private fun gradeToScore(g: String) = when (g.uppercase()) {
        "A+" -> 4.5f; "A" -> 4.3f; "A-" -> 4.0f
        "B+" -> 3.5f; "B" -> 3.3f; "B-" -> 3.0f
        "C+" -> 2.5f; "C" -> 2.3f; "C-" -> 2.0f
        "D+" -> 1.5f; "D" -> 1.0f
        else -> 0f
    }
}

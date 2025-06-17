package kr.ac.kumoh.ce.s2025114794.mysql_db.service

import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.ProfessorDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.StudentDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.AdminDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.entity.Lecture

import kr.ac.kumoh.ce.s2025114794.mysql_db.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 교수 전용 서비스 (학생 정보·강의·성적 조회)
 */
interface ProfessorService {
    fun searchStudents(pno: String, keyword: String): List<ProfessorDto.StudentInfo>
    fun getTimetable(pno: String, year: Int, semester: Int): List<ProfessorDto.TimetableEntry>
    fun getStudentTimetable(sno: String, year: Int, semester: Int): List<StudentDto.TimetableEntry>
    fun getStudentGrades(sno: String): StudentDto.GradeReport
    fun findWithProfessorAndDept(dept_id: Int): List<Lecture>
}

@Service
@Transactional(readOnly = true)
class ProfessorServiceImpl(
    private val professorRepo: ProfessorRepository,
    private val lectureRepo: LectureRepository,
    private val studentRepo: StudentRepository,
    private val enrollmentRepo: EnrollmentRepository
) : ProfessorService {

    // -------------------------------------------------- 학생 검색
    override fun searchStudents(pno: String, keyword: String): List<ProfessorDto.StudentInfo> {
        val deptId = professorRepo.findById(pno).orElseThrow().department!!.deptId
        return studentRepo.findAll().filter {
            it.department?.deptId == deptId && (it.sno.contains(keyword) || it.sname.contains(keyword))
        }.map {
            ProfessorDto.StudentInfo(
                sno = it.sno,
                sname = it.sname,
                gender = it.gender,
                deptName = it.department!!.deptName,
                year = it.year ?: 1,
                phone = it.phone
            )
        }
    }
    override fun findWithProfessorAndDept(dept_id: Int): List<Lecture> {
        return lectureRepo.findWithProfessorAndDept(dept_id)
    }
    // -------------------------------------------------- 교수 강의 시간표
    override fun getTimetable(pno: String, year: Int, semester: Int): List<ProfessorDto.TimetableEntry> {
        // 연·학기 정보가 테이블에 없다면 무시하고 pno 로 필터 (DDL 단순화)
        return lectureRepo.findAll().filter { it.professor?.pno == pno }.map {
            val (timeStr, room) = splitTimeRoom(it.lecTimeRoom)
            ProfessorDto.TimetableEntry(it.lecNo, it.lecName, timeStr, room)
        }
    }

    // -------------------------------------------------- 특정 학생 시간표
    override fun getStudentTimetable(sno: String, year: Int, semester: Int): List<StudentDto.TimetableEntry> {
        return enrollmentRepo.findByStudent_Sno(sno).map {
            val lec = it.lecture!!
            val (timeStr, room) = splitTimeRoom(lec.lecTimeRoom)
            StudentDto.TimetableEntry(lec.lecNo, lec.lecName, timeStr, room)
        }
    }

    // -------------------------------------------------- 특정 학생 성적표
    override fun getStudentGrades(sno: String): StudentDto.GradeReport {
        val enrollments = enrollmentRepo.findByStudent_Sno(sno)
        val details = enrollments.mapNotNull { e ->
            e.grade?.let {
                StudentDto.GradeDetail(
                    lecNo = e.lecture!!.lecNo,
                    lecName = e.lecture!!.lecName,
                    credit = e.lecture!!.credit,
                    grade = it,
                    score = gradeToScore(it)
                )
            }
        }
        val totalCredits = details.sumOf { it.credit }
        val weighted = details.sumOf { it.score.toDouble() * it.credit }
        val gpa = if (totalCredits == 0) 0f else (weighted / totalCredits).toFloat()
        return StudentDto.GradeReport(details.size, totalCredits, String.format("%.2f", gpa).toFloat(), details)
    }

    // -------------------------------------------------- 헬퍼
    private fun splitTimeRoom(raw: String): Pair<String, String> =
        if (raw.contains(",")) raw.split(",").first() to raw.split(",").last()
        else raw to ""

    private fun gradeToScore(g: String) = when (g.uppercase()) {
        "A+" -> 4.5f; "A" -> 4.3f; "A-" -> 4.0f
        "B+" -> 3.5f; "B" -> 3.3f; "B-" -> 3.0f
        "C+" -> 2.5f; "C" -> 2.3f; "C-" -> 2.0f
        "D+" -> 1.5f; "D" -> 1.0f
        else -> 0f
    }
}

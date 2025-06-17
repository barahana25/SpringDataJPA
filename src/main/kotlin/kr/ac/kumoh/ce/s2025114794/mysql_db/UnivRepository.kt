package kr.ac.kumoh.ce.s2025114794.mysql_db.repository

import kr.ac.kumoh.ce.s2025114794.mysql_db.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

// ---- 기본 테이블 ----
interface DepartmentRepository : JpaRepository<Department, String>
interface ProfessorRepository  : JpaRepository<Professor, String>
interface StudentRepository    : JpaRepository<Student, String>
//interface LectureRepository    : JpaRepository<Lecture, String>
interface LectureRepository : JpaRepository<Lecture, String> {
    @Query("""
        SELECT l FROM Lecture l
            JOIN FETCH l.professor p
            JOIN FETCH l.department d
        WHERE (:dept_id IS NULL OR d.deptId = :dept_id)
      """)
    fun findWithProfessorAndDept(@Param("dept_id") dept_id: Int? = null): List<Lecture>
}

// ---- 다대다·복합키 ----
interface EnrollmentRepository : JpaRepository<Enrollment, EnrollmentId> {
    fun findByStudent_Sno(sno: String): List<Enrollment>
    fun findByLecture_LecNo(lecNo: String): List<Enrollment>
}

interface GuidanceRepository : JpaRepository<Guidance, GuidanceId> {
    fun findByProfessor_Pno(pno: String): List<Guidance>
}

// ---- 계정·로그 ----
interface StudentAccountRepository   : JpaRepository<StudentAccount, String>
interface ProfessorAccountRepository : JpaRepository<ProfessorAccount, String>
interface AdminAccountRepository     : JpaRepository<AdminAccount, String>

interface LoginLogRepository : JpaRepository<LoginLog, Long> {
    fun findByUserIdContaining(keyword: String): List<LoginLog>
}
package kr.ac.kumoh.ce.s2025114794.mysql_db.repository

import kr.ac.kumoh.ce.s2025114794.mysql_db.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

// ---- 기본 테이블 ----
interface DepartmentRepository : JpaRepository<Department, String>
interface ProfessorRepository  : JpaRepository<Professor, String> {
    @Query("SELECT p FROM Professor p JOIN FETCH p.department d WHERE p.pno = :pno")
    fun findWithDepartment(@Param("pno") pno: String): Professor?
}

interface StudentRepository    : JpaRepository<Student, String> {
    @Query(
        """
        SELECT s FROM Student s
            JOIN FETCH s.department d
        WHERE d.deptId = :deptId
          AND (s.sno LIKE %:keyword% OR s.sname LIKE %:keyword%)
        """
    )
    fun searchByDepartmentAndKeyword(
        @Param("deptId") deptId: String,
        @Param("keyword") keyword: String
    ): List<Student>

    @Query("SELECT s FROM Student s JOIN FETCH s.department d WHERE s.sno = :sno")
    fun findWithDepartment(@Param("sno") sno: String): Student?
}
//interface LectureRepository    : JpaRepository<Lecture, String>
interface LectureRepository : JpaRepository<Lecture, String> {
    @Query("""
        SELECT l FROM Lecture l
            JOIN FETCH l.professor p
            JOIN FETCH l.department d
        WHERE (:dept_id IS NULL OR d.deptId = :dept_id)
      """)
    fun findWithProfessorAndDept(@Param("dept_id") dept_id: Int? = null): List<Lecture>

    @Query("SELECT l FROM Lecture l WHERE l.professor.pno = :pno")
    fun findByProfessor_Pno(@Param("pno") pno: String): List<Lecture>
}

// ---- 다대다·복합키 ----
interface EnrollmentRepository : JpaRepository<Enrollment, EnrollmentId> {
    @Query("SELECT e FROM Enrollment e WHERE e.student.sno = :sno")
    fun findByStudent_Sno(@Param("sno") sno: String): List<Enrollment>

    @Query("SELECT e FROM Enrollment e WHERE e.lecture.lecNo = :lecNo")
    fun findByLecture_LecNo(@Param("lecNo") lecNo: String): List<Enrollment>
}

interface GuidanceRepository : JpaRepository<Guidance, GuidanceId> {
    @Query("SELECT g FROM Guidance g WHERE g.professor.pno = :pno")
    fun findByProfessor_Pno(@Param("pno") pno: String): List<Guidance>
}

// ---- 계정·로그 ----
interface StudentAccountRepository   : JpaRepository<StudentAccount, String>
interface ProfessorAccountRepository : JpaRepository<ProfessorAccount, String>
interface AdminAccountRepository     : JpaRepository<AdminAccount, String>

interface LoginLogRepository : JpaRepository<LoginLog, Long> {
    @Query("SELECT l FROM LoginLog l WHERE l.userId LIKE %:keyword%")
    fun findByUserIdContaining(@Param("keyword") keyword: String): List<LoginLog>
}
package kr.ac.kumoh.ce.s2025114794.mysql_db

import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.AuthDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.StudentDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.AdminDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.dto.ProfessorDto
import kr.ac.kumoh.ce.s2025114794.mysql_db.service.StudentService
import kr.ac.kumoh.ce.s2025114794.mysql_db.service.ProfessorService
import kr.ac.kumoh.ce.s2025114794.mysql_db.service.AdminService
import kr.ac.kumoh.ce.s2025114794.mysql_db.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*



// ------------------------------- 인증 -------------------------------
@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("/login")
    fun login(@RequestBody req: AuthDto.LoginRequest): ResponseEntity<AuthDto.LoginResponse> {
        return ResponseEntity.ok(authService.login(req))
    }
}

@RestController
@RequestMapping("/api/account")
class AccountController(private val authService: AuthService) {
    @PostMapping("/change-password")
    fun changePassword(
        @AuthenticationPrincipal principal: CustomPrincipal,
        @RequestBody req: AuthDto.ChangePasswordRequest
    ): ResponseEntity<Map<String, Boolean>> {
        authService.changePassword(principal.id, principal.role, req)
        return ResponseEntity.ok(mapOf("success" to true))
    }
}

// ------------------------------- 학생 -------------------------------
@RestController
@RequestMapping("/api/student")
class StudentController(private val studentService: StudentService) {

    @GetMapping("/profile")
    fun profile(@AuthenticationPrincipal principal: CustomPrincipal): ResponseEntity<StudentDto.Profile> =
        ResponseEntity.ok(studentService.getProfile(principal.id))

    @GetMapping("/timetable")
    fun timetable(
        @AuthenticationPrincipal principal: CustomPrincipal,
        @RequestParam year: Int,
        @RequestParam semester: Int
    ) = ResponseEntity.ok(studentService.getTimetable(principal.id, year, semester))

    @GetMapping("/grades")
    fun grades(@AuthenticationPrincipal principal: CustomPrincipal) =
        ResponseEntity.ok(studentService.getGrades(principal.id))
}

// ------------------------------- 교수 -------------------------------
@RestController
@RequestMapping("/api/professor")
class ProfessorController(private val professorService: ProfessorService) {

    @PostMapping("/students")
    fun searchStudents(
        @RequestBody body: Map<String, String>,
        @AuthenticationPrincipal principal: CustomPrincipal
    ) = ResponseEntity.ok(
        professorService.searchStudents(principal.id, body["keyword"].orEmpty())
    )

    @GetMapping("/timetable")
    fun timetable(
        @AuthenticationPrincipal principal: CustomPrincipal,
        @RequestParam year: Int,
        @RequestParam semester: Int
    ) = ResponseEntity.ok(professorService.getTimetable(principal.id, year, semester))


    @GetMapping("/student-timetable")
    fun studentTimetable(
        @RequestParam sno: String,
        @RequestParam year: Int,
        @RequestParam semester: Int
    ) = ResponseEntity.ok(professorService.getStudentTimetable(sno, year, semester))

    @GetMapping("/student-grades")
    fun studentGrades(@RequestParam sno: String) =
        ResponseEntity.ok(professorService.getStudentGrades(sno))

    @GetMapping("/lecture_by_dept_id")
    fun get_lecture_by_dept_id(@RequestParam dept_id: Int) =
        ResponseEntity.ok(professorService.findWithProfessorAndDept(dept_id))
}

// ------------------------------- 관리자 -------------------------------
@RestController
@RequestMapping("/api/admin")
class AdminController(private val adminService: AdminService, private val professorService: ProfessorService) {

    @PostMapping("/accounts")
    fun createAccount(@RequestBody dto: AdminDto.AccountUpsert) =
        ResponseEntity.ok(adminService.createAccount(dto))

    @PutMapping("/accounts/{id}")
    fun updateAccount(
        @PathVariable id: String,
        @RequestBody dto: AdminDto.AccountUpsert
    ) = ResponseEntity.ok(adminService.updateAccount(id, dto))

    @DeleteMapping("/accounts/{id}")
    fun deleteAccount(@PathVariable id: String) =
        ResponseEntity.ok(adminService.deleteAccount(id))

    @PutMapping("/lectures/{lecNo}")
    fun upsertLecture(
        @PathVariable lecNo: String,
        @RequestBody dto: AdminDto.LectureUpsert
    ) = ResponseEntity.ok(adminService.upsertLecture(lecNo, dto))

    @DeleteMapping("/lectures/{lecNo}")
    fun deleteLecture(@PathVariable lecNo: String) =
        ResponseEntity.ok(adminService.deleteLecture(lecNo))

    @GetMapping("/grades/statistics")
    fun gradeStatistics(@RequestParam lecNo: String) =
        ResponseEntity.ok(adminService.getGradeStatistics(lecNo))

    @PostMapping("/login_log")
    fun loginLog(@RequestBody body: Map<String, String>) =
        ResponseEntity.ok(adminService.getLoginLogs(body["keyword"].orEmpty()))

}

// ------------------------------- 공통 -------------------------------

data class CustomPrincipal(val id: String, val role: String)

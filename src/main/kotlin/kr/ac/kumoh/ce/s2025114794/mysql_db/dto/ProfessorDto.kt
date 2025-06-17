package kr.ac.kumoh.ce.s2025114794.mysql_db.dto

object ProfessorDto {
    data class StudentInfo(
        val sno: String,
        val sname: String,
        val gender: String,
        val deptName: String,
        val year: Int,
        val phone: String?
    )

    data class TimetableEntry(
        val lecNo: String,
        val lecName: String,
        val time: String,
        val room: String
    )

    data class StudentTimetable(
        val entries: List<TimetableEntry>
    )

    data class StudentGrades(
        val totalCourses: Int,
        val totalCredits: Int,
        val gpa: Float,
        val details: List<StudentDto.GradeDetail>
    )
}
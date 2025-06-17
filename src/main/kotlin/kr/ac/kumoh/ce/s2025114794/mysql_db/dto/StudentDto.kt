package kr.ac.kumoh.ce.s2025114794.mysql_db.dto

object StudentDto {
    data class Profile(
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

    data class GradeDetail(
        val lecNo: String,
        val lecName: String,
        val credit: Int,
        val grade: String,
        val score: Float
    )

    data class GradeReport(
        val totalCourses: Int,
        val totalCredits: Int,
        val gpa: Float,
        val details: List<GradeDetail>
    )
}
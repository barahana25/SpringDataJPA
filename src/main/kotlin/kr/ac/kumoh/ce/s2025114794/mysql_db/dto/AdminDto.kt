package kr.ac.kumoh.ce.s2025114794.mysql_db.dto

object AdminDto {
    data class AccountUpsert(
        val id: String,
        val password: String,
        val role: String,
        val keyword: String? = null,
        val depName: String? = null,
        val phone: String? = null
    )

    data class LectureUpsert(
        val lecNo: String,
        val lecName: String,
        val credit: Int,
        val deptId: String,
        val lecTimeRoom: String,
        val pno: String
    )

    data class GradeStatistics(
        val totalAverage: Double,
        val midAverage: Double,
        val finalAverage: Double,
        val distribution: Map<String, Int>
    )

    data class LoginLog(
        val id: String,
        val role: String,
        val lastLogin: String
    )
}
package kr.ac.kumoh.ce.s2025114794.mysql_db.dto

data class AuthDto(
    val dummy: String = ""
) {
    data class LoginRequest(val id: String, val password: String)
    data class LoginResponse(val success: Boolean, val role: String, val token: String)
    data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)
}
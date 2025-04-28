package kr.ac.kumoh.ce.s2025114794.mysql_db

import java.time.LocalDate

data class MechanicJoinGunplaDto (
    val mechanicId: Int,
    val name: String,
    val model: String,
    val gunplaId: Int?,
    val title: String?,
    val grade: String?,
    val date: LocalDate?,
    val price: Int?
)
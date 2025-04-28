package kr.ac.kumoh.ce.s2025114794.mysql_db

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
data class Mechanic(
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id: Int,
    var name: String,
    var model: String,
    var manufacturer: String,
    var armor: String,
    var height: Int,
    var weight: Int
)

@Entity
data class Gunpla (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    var mechanic_id: Int,
    var grade: String,
    var date: LocalDate,
    var price: Int,
    var title: String
)
package kr.ac.kumoh.ce.s2025114794.mysql_db.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

// ------------------  Department ------------------
@Entity
@Table(name = "department")
open class Department(
    @Id
    @Column(length = 10)
    var deptId: String = "",

    @Column(nullable = false, length = 50)
    var deptName: String = ""
)

// ------------------  Professor ------------------
@Entity
@Table(name = "professor")
open class Professor(
    @Id
    @Column(length = 10)
    var pno: String = "",

    @Column(nullable = false, length = 50)
    var pname: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    var department: Department? = null
)

// ------------------  Student ------------------
@Entity
@Table(name = "student")
open class Student(
    @Id
    @Column(length = 10)
    var sno: String = "",

    @Column(nullable = false, length = 50)
    var sname: String = "",

    @Column(nullable = false, length = 5)
    var gender: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    var department: Department? = null,

    @Column
    var year: Int? = null,

    @Column(length = 20)
    var phone: String? = null
)

// ------------------  Lecture ------------------
@Entity
@Table(name = "lecture")
open class Lecture(
    @Id
    @Column(length = 10)
    var lecNo: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pno", nullable = false)
    var professor: Professor? = null,

    @Column(nullable = false, length = 100)
    var lecName: String = "",

    @Column(nullable = false)
    var credit: Int = 0,

    @Column(nullable = false, length = 50)
    var lecTimeRoom: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    var department: Department? = null
)

// ------------------  Composite Keys ------------------
@Embeddable
data class EnrollmentId(
    var sno: String = "",
    var lecNo: String = ""
) : Serializable

@Embeddable
data class GuidanceId(
    var pno: String = "",
    var sno: String = ""
) : Serializable

// ------------------  Enrollment ------------------
@Entity
@Table(name = "enrollment")
open class Enrollment(
    @EmbeddedId
    var id: EnrollmentId = EnrollmentId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sno")
    @JoinColumn(name = "sno")
    var student: Student? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecNo")
    @JoinColumn(name = "lec_no")
    var lecture: Lecture? = null,

    @Column(length = 5)
    var grade: String? = null,

    var midScore: Int? = null,
    var finalScore: Int? = null
)

// ------------------  Guidance ------------------
@Entity
@Table(name = "guidance")
open class Guidance(
    @EmbeddedId
    var id: GuidanceId = GuidanceId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pno")
    @JoinColumn(name = "pno")
    var professor: Professor? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sno")
    @JoinColumn(name = "sno")
    var student: Student? = null
)

// ------------------  Account (MappedSuperclass) ------------------
@MappedSuperclass
abstract class Account(
    @Column(nullable = false, length = 255)
    var password: String = "",

    var lastLogin: LocalDateTime? = null
)

// ------------------  StudentAccount ------------------
@Entity
@Table(name = "student_account")
open class StudentAccount(
    @Id
    var sno: String = "",

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sno", insertable = false, updatable = false)
    var student: Student? = null
) : Account()

// ------------------  ProfessorAccount ------------------
@Entity
@Table(name = "professor_account")
open class ProfessorAccount(
    @Id
    var pno: String = "",

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pno", insertable = false, updatable = false)
    var professor: Professor? = null
) : Account()

// ------------------  AdminAccount ------------------
@Entity
@Table(name = "admin_account")
open class AdminAccount(
    @Id
    var adminId: String = ""
) : Account()

// ------------------  LoginLog ------------------
@Entity
@Table(name = "login_log")
open class LoginLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var userId: String = "",
    var role: String = "",
    var lastLogin: LocalDateTime = LocalDateTime.now()
)
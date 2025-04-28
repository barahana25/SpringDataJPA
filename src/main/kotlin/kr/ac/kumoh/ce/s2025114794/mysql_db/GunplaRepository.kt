package kr.ac.kumoh.ce.s2025114794.mysql_db

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GunplaRepository : JpaRepository<Mechanic, Int> {
    @Query("""
        select new kr.ac.kumoh.ce.s2025114794.mysql_db.MechanicJoinGunplaDto(
            m.id as mechanicId, name, model,
           g.id as gunplaId, title, grade, date, price)
       from Mechanic m left outer join Gunpla g
       on m.id = g.mechanic_id
    """)
    fun findMechanicJoinGunpla(): List<MechanicJoinGunplaDto>
}
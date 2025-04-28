package kr.ac.kumoh.ce.s2025114794.mysql_db

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GunplaController(val service: MechanicService) {
    @GetMapping("/")
    fun welcome(): String {
        return "Welcome to the Gunpla Server!"
    }

    @GetMapping("/mechanics")
    fun mechanicList(): List<Mechanic> {
        return service.getAllMechanics()
    }

    @GetMapping("/mechanic/add")
    fun addMechanic(): Map<String, Any> {
        val result = HashMap<String, Any>()

        try {
            service.add(
                Mechanic(0, "네러티브 건담", "RX-9",
                    "Anaheim Electronics",
                    "Gundarium Alloy",
                    21, 40)
            )

            result["result"] = "success"
            return result
        } catch (e: Exception) {
            result["result"] = "failed"
            result["message"] = e.toString()
            return result
        }
    }

    @GetMapping("/mechanic/join/gunpla")
    fun mechanicJoinList(): List<MechanicJoinGunplaDto> {
        return service.getMechanicJoinGunpla()
    }
}
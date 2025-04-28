package kr.ac.kumoh.ce.s2025114794.mysql_db

import org.springframework.stereotype.Service

@Service
class MechanicService(val repository: GunplaRepository) {
    fun getAllMechanics(): List<Mechanic> {
        return repository.findAll()
    }

    fun add(mechanic: Mechanic) {
        if (mechanic.name.isBlank() || mechanic.model.isBlank())
            throw IllegalArgumentException("Name 및 Model은 null일 수 없습니다.")
        repository.save(mechanic)
    }

    fun getMechanicJoinGunpla(): List<MechanicJoinGunplaDto> {
        return repository.findMechanicJoinGunpla()
    }
}
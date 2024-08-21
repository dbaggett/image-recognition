package danny.baggett.infrastructure.repository

import danny.baggett.infrastructure.repository.model.StoredObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoredObjectRepository : JpaRepository<StoredObject, Long> {

    fun findAllByNameIn(names: List<String>): List<StoredObject>
}
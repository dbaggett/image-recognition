package danny.baggett.infrastructure.repository.model

import danny.baggett.model.DetectedObject
import jakarta.persistence.*

@Entity
@Table(name = "stored_object")
class StoredObject(
    @Id
    @SequenceGenerator(name = "stored_object_id_seq", sequenceName = "stored_object_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stored_object_id_seq")
    val id: Long? = null,

    @Column(unique = true)
    val name: String
) {
    fun toDetectedObject() = DetectedObject(id = id, name = name, confidence = 0.0)
}

fun DetectedObject.toStoredObject() = StoredObject(name = name)
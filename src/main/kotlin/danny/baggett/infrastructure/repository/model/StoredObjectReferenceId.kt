package danny.baggett.infrastructure.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class StoredObjectReferenceId(
    @Column(name = "image_id", nullable = false)
    val imageId: Long,

    @Column(name = "stored_object_id", nullable = false)
    val storedObjectId: Long
)
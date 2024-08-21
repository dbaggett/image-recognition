package danny.baggett.infrastructure.repository.model

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "stored_object_reference")
class StoredObjectReference(

    @EmbeddedId
    val storedObjectReferenceId: StoredObjectReferenceId,
)
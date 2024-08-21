package danny.baggett.infrastructure.repository.model

import danny.baggett.model.AnalyzedImage
import jakarta.persistence.*

@Entity
@Table(name = "image")
class PostAnalyzedImage(
    @Id
    @SequenceGenerator(name = "image_id_seq", sequenceName = "image_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_id_seq")
    val id: Long? = null,

    @Column(nullable = false)
    val url: String,

    @Column(nullable = false)
    val label: String,

    @Column(name = "object_recognition_enabled", nullable = false)
    val objectRecognitionEnabled: Boolean,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "stored_object_reference",
        joinColumns = [JoinColumn(name = "image_id")],
        inverseJoinColumns = [JoinColumn(name = "stored_object_id")]
    )
    val storedObjects: Set<StoredObject>
) {
    fun toAnalyzedImage() = AnalyzedImage(
        id = id,
        url = url,
        label = label,
        objectRecognitionEnabled = objectRecognitionEnabled,
        detectedObjects = storedObjects.map { it.toDetectedObject() }.toSet()
    )
}

fun AnalyzedImage.toPostAnalyzedImage() = PostAnalyzedImage(
    url = url,
    label = label,
    objectRecognitionEnabled = objectRecognitionEnabled,
    storedObjects = detectedObjects.map { it.toStoredObject() }.toSet()
)
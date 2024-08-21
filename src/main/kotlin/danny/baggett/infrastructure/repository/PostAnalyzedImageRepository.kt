package danny.baggett.infrastructure.repository

import danny.baggett.infrastructure.repository.model.PostAnalyzedImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostAnalyzedImageRepository : JpaRepository<PostAnalyzedImage, Long> {

    fun findByStoredObjectsNameIn(storedObjectNames: List<String>): List<PostAnalyzedImage>
}
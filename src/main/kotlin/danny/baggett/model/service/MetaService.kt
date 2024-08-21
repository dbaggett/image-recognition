package danny.baggett.model.service

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import danny.baggett.infrastructure.repository.PostAnalyzedImageRepository
import danny.baggett.infrastructure.repository.StoredObjectReferenceRepository
import danny.baggett.infrastructure.repository.StoredObjectRepository
import danny.baggett.infrastructure.repository.model.StoredObjectReference
import danny.baggett.infrastructure.repository.model.StoredObjectReferenceId
import danny.baggett.infrastructure.repository.model.toPostAnalyzedImage
import danny.baggett.model.AnalyzedImage
import danny.baggett.model.DetectedObject
import danny.baggett.model.PreAnalyzedImage
import danny.baggett.model.error.ImageAnalysisError
import danny.baggett.model.error.ImageError
import danny.baggett.model.error.ImageNotFoundError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class MetaService(
    private val imageRecognitionService: ImageRecognitionService,
    private val postAnalyzedImageRepository: PostAnalyzedImageRepository,
    private val storedObjectRepository: StoredObjectRepository,
    private val storedObjectReferenceRepository: StoredObjectReferenceRepository
) {

    private suspend fun analyzedIncludeExistingObjects(
        analyzedImage: AnalyzedImage
    ): Either<ImageError, Pair<AnalyzedImage, Set<DetectedObject>>> = Either.catch {
        val analyzedObjects = analyzedImage.detectedObjects
        val existingObjects = storedObjectRepository
            .findAllByNameIn(analyzedObjects.map { it.name })
            .map { it.toDetectedObject() }
        val existingObjectNames = existingObjects.map { it.name }

        return@catch Pair(
            analyzedImage.copy(
                detectedObjects = analyzedObjects.filter { !existingObjectNames.contains(it.name) }.toSet()
            ),
            existingObjects.toSet()
        )
    }.mapLeft { ImageAnalysisError("Unknown error") }


    private suspend fun associateObjectsAndStoreImage(
        imageAndExistingObjects: Pair<AnalyzedImage, Set<DetectedObject>>
    ): Either<ImageError, AnalyzedImage> = Either.catch {
        val analyzedImage = imageAndExistingObjects.first
        val previouslyDetectedObjects = imageAndExistingObjects.second
        val postAnalyzedImage = postAnalyzedImageRepository.save(analyzedImage.toPostAnalyzedImage())

        val references = previouslyDetectedObjects.mapNotNull { existingObject ->
            return@mapNotNull if (postAnalyzedImage.id != null && existingObject.id != null) {
                StoredObjectReference(
                    storedObjectReferenceId = StoredObjectReferenceId(
                        imageId = postAnalyzedImage.id,
                        storedObjectId = existingObject.id
                    ),
                )
            } else {
                null
            }
        }

        storedObjectReferenceRepository.saveAll(references)

        return@catch postAnalyzedImage.toAnalyzedImage().copy(
            id = postAnalyzedImage.id,
            detectedObjects = analyzedImage.detectedObjects + previouslyDetectedObjects
        )
    }.mapLeft { ImageAnalysisError("Unknown error") }

    suspend fun analyzeAndSave(preAnalyzedImage: PreAnalyzedImage): Either<ImageError, AnalyzedImage> = either {
        val analyzedImage = imageRecognitionService.analyzeImage(preAnalyzedImage).bind()
        val imageAndExistingObjects = analyzedIncludeExistingObjects(analyzedImage).bind()
        associateObjectsAndStoreImage(imageAndExistingObjects).bind()
    }

    suspend fun retrieveSingleImage(imageId: Long): Either<ImageError, AnalyzedImage> =
        withContext(Dispatchers.IO) {
            postAnalyzedImageRepository.findById(imageId)
        }.getOrNull()?.toAnalyzedImage()?.right()
            ?: ImageNotFoundError("Image $imageId not found").left()

    suspend fun retrieveImages(objectNames: List<String>): Either<ImageError, List<AnalyzedImage>> = Either.catch {
        return@catch if (objectNames.isEmpty()) {
            postAnalyzedImageRepository.findAll().map { it.toAnalyzedImage() }.toList()
        } else {
            postAnalyzedImageRepository.findByStoredObjectsNameIn(objectNames).map { it.toAnalyzedImage() }
        }
    }.mapLeft { ImageAnalysisError("Unknown error") }
}
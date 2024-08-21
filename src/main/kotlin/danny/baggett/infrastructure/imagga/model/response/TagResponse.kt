package danny.baggett.infrastructure.imagga.model.response

import danny.baggett.infrastructure.imagga.model.ResponseStatus
import danny.baggett.infrastructure.imagga.model.TagResult
import danny.baggett.model.DetectedObject

data class TagResponse(
    val result: TagResult,
    val status: ResponseStatus
) {
    fun toDetectedObjects(): Set<DetectedObject> = result.tags.mapNotNull { tag ->
        tag.tag["en"]?.let { name ->
            DetectedObject(name = name, confidence = tag.confidence)
        }
    }.toSet()
}
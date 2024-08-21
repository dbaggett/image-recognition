package danny.baggett.infrastructure.api.model

import danny.baggett.model.AnalyzedImage

data class ImageResponse(
    val id: String,
    val label: String,
    val objects: List<String>
)

fun AnalyzedImage.toImageResponse() = ImageResponse(
    id = id.toString(),
    label = label,
    objects = detectedObjects.map { it.name }
)
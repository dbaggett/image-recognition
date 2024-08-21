package danny.baggett.infrastructure.api.model

import danny.baggett.model.PreAnalyzedImage

data class ImageSubmissionRequest(
    val imageUrl: String,
    val label: String?,
    val enableObjectDetection: Boolean? = false
) {
    fun toPreAnalyzedImage() = PreAnalyzedImage(
        url = imageUrl,
        label = label,
        enableObjectDetection = enableObjectDetection ?: false
    )
}
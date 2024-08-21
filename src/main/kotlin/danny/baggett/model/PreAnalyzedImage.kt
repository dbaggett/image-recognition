package danny.baggett.model

data class PreAnalyzedImage(
    val url: String,
    val label: String?,
    val enableObjectDetection: Boolean
)
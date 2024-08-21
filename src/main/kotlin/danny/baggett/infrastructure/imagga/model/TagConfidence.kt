package danny.baggett.infrastructure.imagga.model

data class TagConfidence(
    val confidence: Double,
    val tag: Map<String, String>
)
package danny.baggett.model

data class DetectedObject(
    val id: Long? = null,
    val name: String,
    val confidence: Double
)
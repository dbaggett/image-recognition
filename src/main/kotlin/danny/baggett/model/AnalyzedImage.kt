package danny.baggett.model

data class AnalyzedImage(
    val id: Long? = null,
    val url: String,
    val label: String,
    val objectRecognitionEnabled: Boolean,
    val detectedObjects: Set<DetectedObject>
) {
    companion object {
        val defaultLabelPrefix = "generated"

        fun fromPreAnalyzedImage(
            preAnalyzedImage: PreAnalyzedImage,
            detectedObjects: Set<DetectedObject>,
            confidenceThreshold: Double
        ): AnalyzedImage {
            val label = preAnalyzedImage.label.let { maybeLabel ->
                return@let if (maybeLabel == null) {
                    val highConfidenceObjectNames = detectedObjects
                        .filter { it.confidence >= confidenceThreshold }
                        .map { it.name }

                    val label = highConfidenceObjectNames
                        .joinToString(separator = "-")
                        .let { labels ->
                            if (labels.isNotEmpty()) {
                                "-$labels"
                            } else {
                                labels
                            }
                        }

                    "$defaultLabelPrefix$label"
                } else {
                    maybeLabel
                }
            }

            return AnalyzedImage(
                url = preAnalyzedImage.url,
                label = label,
                objectRecognitionEnabled = preAnalyzedImage.enableObjectDetection,
                detectedObjects = detectedObjects
            )
        }
    }
}
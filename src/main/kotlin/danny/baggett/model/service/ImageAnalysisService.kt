package danny.baggett.model.service

import danny.baggett.model.AnalyzedImage
import danny.baggett.model.DetectedObject
import danny.baggett.model.PreAnalyzedImage
import org.springframework.stereotype.Service

@Service
class ImageAnalysisService(private val imageAnalysisConfig: ImageAnalysisConfig) {

    fun fromPreAnalyzedImage(
        preAnalyzedImage: PreAnalyzedImage,
        detectedObjects: Set<DetectedObject>
    ) = AnalyzedImage.fromPreAnalyzedImage(preAnalyzedImage, detectedObjects, imageAnalysisConfig.confidenceThreshold)
}
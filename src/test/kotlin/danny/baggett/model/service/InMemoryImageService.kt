package danny.baggett.model.service

import arrow.core.Either
import arrow.core.right
import danny.baggett.model.AnalyzedImage
import danny.baggett.model.DetectedObject
import danny.baggett.model.PreAnalyzedImage
import danny.baggett.model.error.ImageError

class InMemoryImageService(private val imageAnalysisConfig: ImageAnalysisConfig) : ImageRecognitionService {

    override suspend fun analyzeImage(preAnalyzedImage: PreAnalyzedImage): Either<ImageError, AnalyzedImage> {
        return AnalyzedImage.fromPreAnalyzedImage(
            preAnalyzedImage,
            setOf(
                DetectedObject(name = "plane", confidence = 90.0),
                DetectedObject(name = "train", confidence = 82.0),
                DetectedObject(name = "automobile", confidence = 50.0)
            ),
            imageAnalysisConfig.confidenceThreshold
        ).right()
    }
}
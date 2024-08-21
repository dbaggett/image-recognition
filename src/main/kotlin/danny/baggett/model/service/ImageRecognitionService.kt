package danny.baggett.model.service

import arrow.core.Either
import danny.baggett.model.AnalyzedImage
import danny.baggett.model.PreAnalyzedImage
import danny.baggett.model.error.ImageError

interface ImageRecognitionService {

    suspend fun analyzeImage(preAnalyzedImage: PreAnalyzedImage): Either<ImageError, AnalyzedImage>
}
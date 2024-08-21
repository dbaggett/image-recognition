package danny.baggett.infrastructure.imagga

import arrow.core.Either
import danny.baggett.infrastructure.imagga.model.response.TagResponse
import danny.baggett.model.AnalyzedImage
import danny.baggett.model.PreAnalyzedImage
import danny.baggett.model.error.ImageAnalysisError
import danny.baggett.model.error.ImageError
import danny.baggett.model.error.ImageException
import danny.baggett.model.service.ImageAnalysisService
import danny.baggett.model.service.ImageRecognitionService
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.util.*

@Service
@Profile("dev")
class ImaggaImageRecognitionService(
    private val imaggaConfig: ImaggaConfig,
    private val imageAnalysisService: ImageAnalysisService
) : ImageRecognitionService {

    override suspend fun analyzeImage(preAnalyzedImage: PreAnalyzedImage): Either<ImageError, AnalyzedImage> {
        return Either.catch {
            val credentials = "${imaggaConfig.apiKey}:${imaggaConfig.secret}"

            if (preAnalyzedImage.enableObjectDetection) {
                val response = imaggaApiClient
                    .get()
                    .uri("${imaggaConfig.baseUrl}/tags?image_url=${preAnalyzedImage.url}")
                    .header(
                        "Authorization",
                        "Basic ${base64Encoder.encodeToString(credentials.toByteArray())}"
                    )
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError) { request, response ->
                        when (response.statusCode) {
                            HttpStatus.BAD_REQUEST ->
                                throw ImageException(ImageAnalysisError("Invalid image url: ${preAnalyzedImage.url}"))

                            HttpStatus.UNAUTHORIZED ->
                                throw ImageException(ImageAnalysisError("Unauthorized"))

                            else -> throw ImageException(ImageAnalysisError("Unknown error"))
                        }
                    }
                    .onStatus(HttpStatusCode::is5xxServerError) { request, response ->
                        throw ImageException(ImageAnalysisError("Unknown error"))
                    }
                    .body(TagResponse::class.java)

                val detectedObjects = response?.toDetectedObjects() ?: emptySet()

                imageAnalysisService.fromPreAnalyzedImage(preAnalyzedImage, detectedObjects)
            } else {
                imageAnalysisService.fromPreAnalyzedImage(preAnalyzedImage, emptySet())
            }
        }.mapLeft { throwable ->
            return@mapLeft when (throwable) {
                is ImageException -> throwable.error
                else -> ImageAnalysisError("Unknown error")
            }
        }
    }

    companion object {
        private val base64Encoder = Base64.getEncoder()
        private val imaggaApiClient = RestClient.builder().build()
    }
}
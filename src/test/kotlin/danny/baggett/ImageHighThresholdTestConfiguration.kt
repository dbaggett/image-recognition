package danny.baggett

import danny.baggett.model.service.ImageAnalysisConfig
import danny.baggett.model.service.ImageRecognitionService
import danny.baggett.model.service.InMemoryImageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@TestConfiguration
class ImageHighThresholdTestConfiguration {

    @Bean
    @Profile("test")
    fun imageRecognitionService(): ImageRecognitionService = InMemoryImageService(
        ImageAnalysisConfig(confidenceThreshold = 95.0)
    )
}
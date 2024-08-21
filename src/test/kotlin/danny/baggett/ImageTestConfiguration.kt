package danny.baggett

import danny.baggett.model.service.ImageAnalysisConfig
import danny.baggett.model.service.ImageRecognitionService
import danny.baggett.model.service.InMemoryImageService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@TestConfiguration
class ImageTestConfiguration {

    @Bean
    @Profile("test")
    fun imageRecognitionService(): ImageRecognitionService = InMemoryImageService(
        ImageAnalysisConfig(confidenceThreshold = 90.0)
    )
}
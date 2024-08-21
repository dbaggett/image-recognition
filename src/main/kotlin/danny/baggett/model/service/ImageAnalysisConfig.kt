package danny.baggett.model.service

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("analysis")
data class ImageAnalysisConfig(val confidenceThreshold: Double)
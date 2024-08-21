package danny.baggett.infrastructure.imagga

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "imagga")
data class ImaggaConfig(
    val baseUrl: String,
    val apiKey: String,
    val secret: String
)
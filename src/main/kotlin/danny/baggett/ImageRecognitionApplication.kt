package danny.baggett

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ImageRecognitionApplication

fun main(args: Array<String>) {
    runApplication<ImageRecognitionApplication>(*args)
}
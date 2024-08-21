package danny.baggett.infrastructure.api

import danny.baggett.ImageLowThresholdTestConfiguration
import danny.baggett.TestDataExtension
import danny.baggett.infrastructure.api.ImageControllerTests.Companion.client
import danny.baggett.infrastructure.api.model.ImageResponse
import danny.baggett.infrastructure.api.model.ImageSubmissionRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(TestDataExtension::class)
@Import(ImageLowThresholdTestConfiguration::class)
class ImageAnalysisLowThresholdTests {

    @LocalServerPort lateinit var port: Integer

    @Test
    fun shouldReturnWithGeneratedLabelOnMissingLabel() {
        val request = HttpEntity(
            ImageSubmissionRequest(
                imageUrl = "http://test.com/image.png",
                label =null,
                enableObjectDetection = true
            )
        )

        val response = client.exchange(
            "http://localhost:$port/images",
            HttpMethod.POST,
            request,
            ImageResponse::class.java
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals("generated-plane-train-automobile", response.body?.label)
    }
}
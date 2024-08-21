package danny.baggett.infrastructure.api

import danny.baggett.ImageTestConfiguration
import danny.baggett.TestDataExtension
import danny.baggett.infrastructure.api.model.ImageResponse
import danny.baggett.infrastructure.api.model.ImageSubmissionRequest
import danny.baggett.infrastructure.repository.PostAnalyzedImageRepository
import danny.baggett.infrastructure.repository.StoredObjectReferenceRepository
import danny.baggett.infrastructure.repository.StoredObjectRepository
import danny.baggett.infrastructure.repository.model.PostAnalyzedImage
import danny.baggett.infrastructure.repository.model.StoredObject
import danny.baggett.infrastructure.repository.model.StoredObjectReference
import danny.baggett.infrastructure.repository.model.StoredObjectReferenceId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestTemplate

@Import(ImageTestConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(TestDataExtension::class)
class ImageControllerTests {
    @Autowired private lateinit var postAnalyzedImageRepository: PostAnalyzedImageRepository
    @Autowired private lateinit var storedObjectRepository: StoredObjectRepository
    @Autowired private lateinit var storedObjectReferenceRepository: StoredObjectReferenceRepository

    @LocalServerPort lateinit var port: Integer

    @BeforeEach
    fun setup() {
        runBlocking {
            postAnalyzedImageRepository.deleteAll()
            storedObjectRepository.deleteAll()
            storedObjectReferenceRepository.deleteAll()
        }
    }

    @Test
    fun shouldReturnSuccessfulResponseOnImagePost() {
        val request = HttpEntity(
            ImageSubmissionRequest(
                imageUrl = "http://test.com/image.png",
                label = "test",
                enableObjectDetection = true
            )
        )

        val response = client.exchange(
            "http://localhost:${port}/images",
            HttpMethod.POST,
            request,
            ImageResponse::class.java
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun shouldReturnWithSetLabel() {
        val request = HttpEntity(
            ImageSubmissionRequest(
                imageUrl = "http://test.com/image.png",
                label = "test",
                enableObjectDetection = true
            )
        )

        val response = client.exchange(
            "http://localhost:${port}/images",
            HttpMethod.POST,
            request,
            ImageResponse::class.java
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals("test", response.body?.label)
    }

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
            "http://localhost:${port}/images",
            HttpMethod.POST,
            request,
            ImageResponse::class.java
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals("generated-plane", response.body?.label)
    }

    @Test
    fun getAllImagesShouldBeSuccessful() {
        postAnalyzedImageRepository.save(postAnalyzedImage1)
        postAnalyzedImageRepository.save(postAnalyzedImage2)

        val response = client.exchange(
            "http://localhost:${port}/images",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<ImageResponse>>() {}
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(2, response.body?.size)
    }

    @Test
    fun getImageBySingleNameShouldBeSuccessful() {
        val storedImage1 = postAnalyzedImageRepository.save(postAnalyzedImage1)
        postAnalyzedImageRepository.save(postAnalyzedImage2)
        val storedObject = storedObjectRepository.save(storedObjectPlane)
        storedObjectReferenceRepository.save(createStoredObjectReference(storedImage1, storedObject))

        val response = client.exchange(
            "http://localhost:${port}/images?objects=plane",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<ImageResponse>>() {}
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(1, response.body?.size)
        Assertions.assertEquals("plane", response.body?.first()?.objects?.first())
    }

    @Test
    fun getImageByTwoNamesShouldBeSuccessful() {
        val storedImage1 = postAnalyzedImageRepository.save(postAnalyzedImage1)
        val storedImage2 = postAnalyzedImageRepository.save(postAnalyzedImage2)
        val storedObject1 = storedObjectRepository.save(storedObjectPlane)
        val storedObject2 = storedObjectRepository.save(storedObjectTrain)
        storedObjectReferenceRepository.save(createStoredObjectReference(storedImage1, storedObject1))
        storedObjectReferenceRepository.save(createStoredObjectReference(storedImage2, storedObject2))

        val response = client.exchange(
            "http://localhost:${port}/images?objects=plane,train",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<ImageResponse>>() {}
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(2, response.body?.size)
    }

    companion object {
        val client = RestTemplate()

        var confidenceThreshold: Double = 90.0

        fun createStoredObjectReference(image: PostAnalyzedImage, storedObject: StoredObject) = StoredObjectReference(
            storedObjectReferenceId = StoredObjectReferenceId(
                imageId = image.id ?: 0L,
                storedObjectId = storedObject.id ?: 0L
            )
        )

        val storedObjectPlane = StoredObject(name = "plane")
        val storedObjectTrain = StoredObject(name = "train")
        val storedObjectAutomobile = StoredObject(name = "automobile")

        val postAnalyzedImage1 = PostAnalyzedImage(
            url = "http://test.com/image.png",
            label = "test1",
            objectRecognitionEnabled = true,
            storedObjects = emptySet()
        )

        val postAnalyzedImage2 = PostAnalyzedImage(
            url = "http://test.com/image.png",
            label = "test2",
            objectRecognitionEnabled = true,
            storedObjects = emptySet()
        )

        val postAnalyzedImage3 = PostAnalyzedImage(
            url = "http://test.com/image.png",
            label = "test3",
            objectRecognitionEnabled = true,
            storedObjects = emptySet()
        )

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("analysis.confidenceThreshold") { confidenceThreshold }
        }
    }
}
package danny.baggett

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.PostgreSQLContainer

class TestDataExtension : BeforeAllCallback, AfterAllCallback {

    override fun beforeAll(extensionContext: ExtensionContext) {
        postgres.start()

        System.setProperty("spring.datasource.url", postgres.jdbcUrl)
        System.setProperty("spring.datasource.username", postgres.username)
        System.setProperty("spring.datasource.password", postgres.password)
        System.setProperty("spring.profiles.active", "test")
    }

    override fun afterAll(extensionContext: ExtensionContext) {
        postgres.stop()
    }

    companion object {
        val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine")
    }
}
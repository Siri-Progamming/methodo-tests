package kat.siri.test.dao

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kat.siri.test.model.Book
import kat.siri.test.port.BookRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest
class BookDaoIntegrationTest {

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }
    }

    @Autowired
    private lateinit var bookRepository: BookRepository

    //Nettoyage BDD avant chaque test
    @BeforeEach
    fun setUp() {
        bookRepository.deleteAll()
    }


    @Test
    fun `should save and retrieve a book successfully`() {
        // Given
        val book = Book(
            title = "Test Book",
            author = "Test Author"
        )

        // When
        val savedBook = bookRepository.save(book)
        val foundBook = bookRepository.findById(savedBook.id!!)

        // Then
        foundBook.isPresent shouldBe true
        foundBook.get().apply {
            id shouldNotBe null
            title shouldBe "Test Book"
            author shouldBe "Test Author"
        }
    }

    @Test
    fun `should find book by title`() {
        // Given
        val book = Book(title = "Unique Title", author = "Some Author")
        bookRepository.save(book)

        // When
        val foundBook = bookRepository.findByTitle("Unique Title")

        // Then
        foundBook shouldNotBe null
        foundBook?.title shouldBe "Unique Title"
    }
}
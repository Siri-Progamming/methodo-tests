package kat.siri.test.dao

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.validation.ConstraintViolationException
import kat.siri.test.model.Book
import kat.siri.test.port.BookRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest
class BookDaoIntegrationTest {

    // Container PostgreSQL
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

    private lateinit var testBook: Book

    //Nettoyage BDD avant chaque test
    @BeforeEach
    fun setUp() {
        bookRepository.deleteAll()
        testBook = Book(title = "Test Book", author = "Test Author")
    }

    @Test
    fun `should create new book`() {
        val savedBook = bookRepository.save(testBook)

        assertNotNull(savedBook.id)
        assertEquals(testBook.title, savedBook.title)
        assertEquals(testBook.author, savedBook.author)
    }

    @Test
    fun `should find book by id`() {
        val savedBook = bookRepository.save(testBook)
        val foundBook = bookRepository.findById(savedBook.id!!).orElse(null)

        assertNotNull(foundBook)
        assertEquals(savedBook.id, foundBook.id)
        assertEquals(savedBook.title, foundBook.title)
        assertEquals(savedBook.author, foundBook.author)
    }

    @Test
    fun `should update book`() {
        val savedBook = bookRepository.save(testBook)
        savedBook.title = "Updated Title"
        savedBook.author = "Updated Author"

        val updatedBook = bookRepository.save(savedBook)

        assertEquals("Updated Title", updatedBook.title)
        assertEquals("Updated Author", updatedBook.author)
    }

    @Test
    fun `should delete book`() {
        val savedBook = bookRepository.save(testBook)
        bookRepository.deleteById(savedBook.id!!)

        val foundBook = bookRepository.findById(savedBook.id!!).orElse(null)
        assertNull(foundBook)
    }

    @Test
    fun `should find all books`() {
        val book1 = bookRepository.save(Book(title = "Book 1", author = "Author 1"))
        val book2 = bookRepository.save(Book(title = "Book 2", author = "Author 2"))

        val allBooks = bookRepository.findAll()

        assertEquals(2, allBooks.size)
        assertTrue(allBooks.any { it.id == book1.id })
        assertTrue(allBooks.any { it.id == book2.id })
    }

    @Test
    fun `should throw exception when title is blank`() {
        val invalidBook = Book(title = "", author = "Test Author")

        assertThrows<ConstraintViolationException> {
            bookRepository.save(invalidBook)
        }
    }

    @Test
    fun `should throw exception when author is blank`() {
        val invalidBook = Book(title = "Test Title", author = "")

        assertThrows<ConstraintViolationException> {
            bookRepository.save(invalidBook)
        }
    }

    @Test
    fun `should find book by title`() {
        val book = Book(title = "Unique Title", author = "Some Author")
        bookRepository.save(book)

        val foundBook = bookRepository.findByTitle("Unique Title")[0]

        foundBook shouldNotBe null
        foundBook.title shouldBe "Unique Title"
    }
}

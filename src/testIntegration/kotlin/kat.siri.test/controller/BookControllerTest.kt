package kat.siri.test.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import kat.siri.test.dto.BookDTO
import kat.siri.test.model.Book
import kat.siri.test.usecase.BookService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.fasterxml.jackson.databind.ObjectMapper

@WebMvcTest(BookController::class)
class BookControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {
    @MockkBean
    lateinit var bookService: BookService

    companion object {
        private const val BOOKS_ENDPOINT = "/books"
    }

    @Test
    fun `should return list of books successfully`() {
        val mockBooks = listOf(
            Book(id = 1, title = "Book 1", author = "Author 1"),
            Book(id = 2, title = "Book 2", author = "Author 2")
        )
        val expectedDTOs = mockBooks.map {
            BookDTO(id = it.id, title = it.title, author = it.author)
        }

        every { bookService.listBooks() } returns mockBooks

        mockMvc.perform(get(BOOKS_ENDPOINT))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Book 1"))
            .andExpect(jsonPath("$[1].title").value("Book 2"))

        verify { bookService.listBooks() }
    }

    @Test
    fun `should create book successfully`() {
        val bookDTO = BookDTO(title = "New Book", author = "New Author")
        val createdBook = Book(id = 0, title = bookDTO.title, author = bookDTO.author)

        // Setup mock behavior
        every { bookService.createBook(any()) } returns createdBook

        // Perform request and validate
        mockMvc.perform(post(BOOKS_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookDTO))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(createdBook.id))
            .andExpect(jsonPath("$.title").value(createdBook.title))
            .andExpect(jsonPath("$.author").value(createdBook.author))

        // Verify service method was called with correct parameter
        verify { bookService.createBook(withArg<Book> {
            assert(it.title == "New Book")
            assert(it.author == "New Author")
        }) }
    }

    @Test
    fun `should handle book creation failure`() {
        val bookDTO = BookDTO(title = "Problematic Book", author = "Problematic Author")

        every { bookService.createBook(any()) } throws RuntimeException("Service error")

        // Perform request and validate
        mockMvc.perform(post(BOOKS_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookDTO))
        )
            .andExpect(status().isInternalServerError)

        // Verify service method was called
        verify { bookService.createBook(any()) }
    }
}
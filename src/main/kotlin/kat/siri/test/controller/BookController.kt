package kat.siri.test.controller

import kat.siri.test.dto.BookDTO
import kat.siri.test.usecase.BookService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/books")
class BookController {
    private val logger = LoggerFactory.getLogger(BookController::class.java)

    @Autowired
    lateinit var bookService: BookService

    @PostMapping
    fun createBook(@RequestBody book: BookDTO): ResponseEntity<BookDTO> {
        return try {
            val entity = book.toEntity()
            val bookCreated = bookService.createBook(entity)
            ResponseEntity.status(HttpStatus.CREATED).body(bookCreated.toDTO())
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping
    fun getBooks(
        @RequestParam(value = "language", required = false) language: String?
    ): List<BookDTO> {
        return bookService.getBooks().map { it.toDTO() }
    }

    @GetMapping("/title/{title}")
    fun getBooksByTitle(
        @PathVariable title: String
    ): List<BookDTO> {
        return bookService.getBooksByTitle(title).map { it.toDTO() }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(
        @PathVariable id: Long
    ): Unit {
        bookService.deleteById(id)
    }

    @PatchMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @RequestBody book: BookDTO
    ): ResponseEntity<BookDTO> {
        return try {
            val bookUpdated = bookService.updateBook(book.toEntity(), id)
            ResponseEntity.ok(bookUpdated.toDTO())
        } catch (e: ResponseStatusException) {
            ResponseEntity.status(e.statusCode).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}

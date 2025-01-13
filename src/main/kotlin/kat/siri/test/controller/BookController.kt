package kat.siri.test.controller

import kat.siri.test.dto.BookDTO
import kat.siri.test.model.Book
import kat.siri.test.usecase.BookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController {

    @Autowired
    lateinit var bookService: BookService

    @GetMapping
    fun getBooks(
        @RequestParam(value = "language", required = false) language: String?
    ): List<BookDTO> {
        return bookService.listBooks().map { book -> book.toDTO() }
    }

    @PostMapping
    fun createBook(@RequestBody book: BookDTO): ResponseEntity<Book> {
        return try {
            val bookCreated = bookService.createBook(book.toEntity())
            ResponseEntity.status(HttpStatus.CREATED).body(bookCreated)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

}
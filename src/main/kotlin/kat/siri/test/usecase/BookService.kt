package kat.siri.test.usecase

import jakarta.validation.Valid
import kat.siri.test.model.Book
import kat.siri.test.port.BookRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import org.springframework.web.server.ResponseStatusException

@Service
@Validated
class BookService(
    private val bookRepository: BookRepository,
) {

    fun createBook(@Valid book: Book): Book {
        return bookRepository.save(book)
    }

    fun getBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.title }
    }

    fun getBooksByTitle(title: String): List<Book> {
        val books = bookRepository.findByTitle(title)
        if (books.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return bookRepository.findByTitle(title)
    }

    fun getBookById(id: Long): Book {
        return bookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Book with ID $id not found") }
    }

    fun deleteById(id: Long) {
        this.getBookById(id)
        bookRepository.deleteById(id)
    }

    fun updateBook(@Valid book: Book, id: Long): Book {
        val existingBook = this.getBookById(id)

        existingBook.title = book.title
        existingBook.author = book.author

        return bookRepository.save(existingBook)
    }
}

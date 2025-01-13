package kat.siri.test.usecase

import kat.siri.test.model.Book
import kat.siri.test.port.BookRepository
import org.springframework.stereotype.Service
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated

@Service
@Validated
class BookService(
    private val bookRepository: BookRepository,
) {

    fun createBook(@Valid book: Book): Book {
        return bookRepository.save(book)
    }

    fun listBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.title }
    }
}

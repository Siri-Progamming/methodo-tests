package kat.siri.test.usecase

import kat.siri.test.model.Book
import kat.siri.test.port.BookRepository

class BookService(private val bookRepository: BookRepository) {

    fun createBook(book: Book): Book {
        return bookRepository.save(book)
    }

    fun listBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.title }
    }
}
package kat.siri.test.port

import kat.siri.test.model.Book

interface BookRepository {
    fun save(book: Book): Book
    fun findAll(): List<Book>
}
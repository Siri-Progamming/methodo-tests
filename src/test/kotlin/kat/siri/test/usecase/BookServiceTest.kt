package kat.siri.test.usecase

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.validation.Validation
import kat.siri.test.model.Book
import kat.siri.test.port.BookRepository

class BookServiceTest : FunSpec({
    val bookRepository = mockk<BookRepository>()
    val validator = Validation.buildDefaultValidatorFactory().validator
    val bookService = BookService(bookRepository)

    //Génération d'un book aléatoire
    val bookArb: Arb<Book> = Arb.bind(
        Arb.long(1L..1000L),
        Arb.string(3, 100),
        Arb.string(3, 50)
    ) { id, title, author -> Book(id, title, author) }

    //Génération d'une liste de books aléatoire (entre 1 & 10)
    val bookListArb: Arb<List<Book>> = Arb.list(bookArb, 1..10)

    context("Handmade Tests") {
        test("should create a book") {
            // Arrange
            val newBook = Book(id = 1, title = "Clean Code", author = "Robert C. Martin")
            every { bookRepository.save(newBook) } returns newBook

            // Act
            val result = bookService.createBook(newBook)

            // Assert
            result shouldBe newBook
            verify { bookRepository.save(newBook) }
        }

        test("should list all books order ASC by title") {
            val books = listOf(
                Book(id = 1, title = "Refactoring", author = "Robert C. Martin"),
                Book(id = 3, title = "Z", author = "Z"),
                Book(id = 2, title = "Clean Code", author = "Martin Fowler"),
            )
            every { bookRepository.findAll() } returns books

            val result = bookService.listBooks()

            result shouldBe books.sortedBy { it.title }
            verify { bookRepository.findAll() }
        }
    }

    context("Property Tests") {
        test("saving a book should preserve its properties") {
            checkAll(10, bookArb) { book ->
                every { bookRepository.save(book) } returns book

                val savedBook = bookRepository.save(book)

                savedBook.id shouldBe book.id
                savedBook.title shouldBe book.title
                savedBook.author shouldBe book.author
                verify(exactly = 1) { bookRepository.save(book) }
            }
        }

        test("la liste des livres retournée contient tous les éléments stockés") {
            checkAll(10, bookListArb) { storedBooks ->
                every { bookRepository.findAll() } returns storedBooks

                val result = bookService.listBooks()

                result.size shouldBe storedBooks.size
                result.containsAll(storedBooks) shouldBe true
            }
        }

        test("la liste des livres retournée doit être triée par titre") {
            checkAll(10, bookListArb) { storedBooks ->
                every { bookRepository.findAll() } returns storedBooks.sortedBy { it.title }

                val result = bookService.listBooks()

                result shouldBe storedBooks.sortedBy { it.title }
                verify { bookRepository.findAll() }
            }
        }
    }

})

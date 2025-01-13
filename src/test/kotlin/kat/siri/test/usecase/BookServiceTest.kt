package kat.siri.test.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kat.siri.test.model.Book
import kat.siri.test.port.BookRepository

class BookServiceTest : FunSpec({
    val bookRepository = mockk<BookRepository>()
    val bookService = BookService(bookRepository)

    //Génération d'un book aléatoire
    val bookArb: Arb<Book> = Arb.bind(
        Arb.long(1L..1000L),
        Arb.string(3, 100),
        Arb.string(3, 50)
    ) { id, title, author -> Book(id, title, author) }

    //Génération d'une liste de books aléatoire (entre
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

        test("should fail because title is empty") {
            shouldThrow<IllegalArgumentException> {
                // Arrange
                val newBook = Book(id = 1, title = "", author = "Robert C. Martin")
                every { bookRepository.save(newBook) } returns newBook

                // Act
                bookService.createBook(newBook)
            }
        }

        test("should fail because author is empty") {
            shouldThrow<IllegalArgumentException> {
                // Arrange
                val newBook = Book(id = 1, title = "Harry Potter", author = "")
                every { bookRepository.save(newBook) } returns newBook

                // Act
                bookService.createBook(newBook)
            }
        }

        test("should list all books order ASC by title") {
            // Arrange
            val books = listOf(
                Book(id = 1, title = "Refactoring", author = "Robert C. Martin"),
                Book(id = 3, title = "Z", author = "Z"),
                Book(id = 2, title = "Clean Code", author = "Martin Fowler"),
            )
            every { bookRepository.findAll() } returns books

            // Act
            val result = bookService.listBooks()

            // Assert
            result shouldBe books.sortedBy { it.title }
            verify { bookRepository.findAll() }
        }
    }

    context("Property Tests") {
        test("saving a book should preserve its properties") {
            checkAll(iterations = 10, bookArb) { book ->
                // Given
                every { bookRepository.save(book) } returns book

                // When
                val savedBook = bookRepository.save(book)

                // Then
                savedBook.id shouldBe book.id
                savedBook.title shouldBe book.title
                savedBook.author shouldBe book.author
                verify(exactly = 1) { bookRepository.save(book) }
            }
        }

        test("la liste des livres retournée contient tous les éléments stockés") {
            checkAll(iterations = 10, bookListArb) { storedBooks ->
                // Arrange
                every { bookRepository.findAll() } returns storedBooks

                // Act
                val result = bookService.listBooks()

                // Assert
                result.size shouldBe storedBooks.size
                result.containsAll(storedBooks) shouldBe true
            }
        }

        test("la liste des livres retournée doit être triée par titre") {
            checkAll(iterations = 10, bookListArb)  { storedBooks ->
                println(storedBooks)
                // Arrange
                every { bookRepository.findAll() } returns storedBooks.sortedBy { it.title }

                // Act
                val result = bookService.listBooks()

                // Assert
                result shouldBe storedBooks.sortedBy { it.title }
                verify { bookRepository.findAll() }
            }
        }
    }

})
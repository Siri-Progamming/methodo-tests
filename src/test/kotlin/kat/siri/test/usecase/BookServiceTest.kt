package kat.siri.test.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
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
import kat.siri.test.model.Book
import kat.siri.test.port.BookRepository
import java.sql.SQLIntegrityConstraintViolationException
import java.util.*

class BookServiceTest : FunSpec({
    val bookRepository = mockk<BookRepository>()
    val bookService = BookService(bookRepository)

    //Génération d'un book aléatoire
    val bookArb: Arb<Book> = Arb.bind(
        Arb.long(1L..1000L),
        Arb.string(3, 100),
        Arb.string(3, 50)
    ) { id, title, author -> Book(id, title, author) }

    //Génération d'une liste de books aléatoire (entre 1 & 10)
    val bookListArb: Arb<List<Book>> = Arb.list(bookArb, 1..10)
    val stringArb: Arb<String> = Arb.string(3, 100)
    val longArb: Arb<Long> = Arb.long(1L..1000L)

    context("Handmade Tests") {
        test("should create a book") {
            val newBook = Book(id = 1, title = "Clean Code", author = "Robert C. Martin")
            every { bookRepository.save(newBook) } returns newBook

            val result = bookService.createBook(newBook)

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

            val result = bookService.getBooks()

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

                val result = bookService.getBooks()

                result.size shouldBe storedBooks.size
                result.containsAll(storedBooks) shouldBe true
            }
        }

        test("la liste des livres retournée doit être triée par titre") {
            checkAll(10, bookListArb) { storedBooks ->
                every { bookRepository.findAll() } returns storedBooks.sortedBy { it.title }

                val result = bookService.getBooks()

                result shouldBe storedBooks.sortedBy { it.title }
                verify { bookRepository.findAll() }
            }
        }

        test("recherche par titre renvoie les bons livres") {
            checkAll(10, bookArb, stringArb) { book, title ->
                val books = listOf(book.copy(title = title))
                every { bookRepository.findByTitle(title) } returns books

                val result = bookService.getBooksByTitle(title)

                result shouldContainExactly books
                verify { bookRepository.findByTitle(title) }
            }
        }
    }

    test("récupération par ID renvoie le bon livre") {
        checkAll(10, bookArb) { book ->
            every { book.id?.let { bookRepository.findById(it) } } returns Optional.of(book)

            val result = book.id?.let { bookService.getBookById(it) }

            result shouldBe book
            verify { book.id?.let { bookRepository.findById(it) } }
        }
    }

    test("récupération par ID lève une exception si livre non trouvé") {
        checkAll(10, longArb) { id ->
            every { bookRepository.findById(id) } returns Optional.empty()

            shouldThrow<IllegalArgumentException> {
                bookService.getBookById(id)
            }

            verify { bookRepository.findById(id) }
        }
    }

    test("mise à jour d'un livre préserve ses nouvelles propriétés") {
        checkAll(10, bookArb, longArb) { book, id ->
            val updatedBook = book.copy(title = "New Title", author = "New Author")
            every { bookRepository.findById(id) } returns Optional.of(book)
            every { bookRepository.save(any()) } returns updatedBook

            val result = bookService.updateBook(updatedBook, id)

            result.title shouldBe updatedBook.title
            result.author shouldBe updatedBook.author
            result.id shouldBe book.id
            verify(exactly = 1) { bookRepository.save(updatedBook) }
        }
    }

    test("suppression d'un livre fonctionne") {
        checkAll(10, longArb) { id ->
            every { bookRepository.findById(id) } returns Optional.empty()
            every { bookRepository.deleteById(id) } returns Unit


            bookService.deleteById(id)

            verify { bookRepository.deleteById(id) }
        }
    }

    test("suppression d'un livre lève une exception si le livre n'existe pas") {
        checkAll(10, longArb) { id ->
            every { bookRepository.existsById(id) } returns false

            shouldThrow<IllegalArgumentException> {
                bookService.deleteById(id)
            }

            verify(exactly = 0) { bookRepository.deleteById(id) }
        }
    }
})

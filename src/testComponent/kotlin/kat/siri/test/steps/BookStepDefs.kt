package kat.siri.test.steps

import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.collections.shouldContainAll
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import kat.siri.test.dto.BookDTO
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookStepDefs {

    @LocalServerPort
    private var port: Int? = 0

    private var lastResponse: ValidatableResponse? = null

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Given("I create a book with title {string} and author {string}")
    fun createBook(title: String, author: String) {
        lastResponse = given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                {
                    "title": "$title",
                    "author": "$author"
                }
            """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
            .log().all()
            .statusCode(201)
    }

    @When("I get all books")
    fun getAllBooks() {
        lastResponse = given()
            .`when`()
            .get("/books")
            .then()
            .log().all()
            .statusCode(200)
    }

    @Then("the response should contain the following books")
    fun verifyBooksList(expectedBooks: List<Map<String, String>>) {
        val actualBooks = lastResponse?.extract()?.body()?.jsonPath()?.getList<Map<String, String>>("$") ?: emptyList()

        val expectedBooksSet = expectedBooks.map { mapOf("title" to it["title"], "author" to it["author"]) }.toSet()
        val actualBooksSet = actualBooks.map { mapOf("title" to it["title"], "author" to it["author"]) }.toSet()

        actualBooksSet shouldContainAll expectedBooksSet
    }

    @When("I search for books by title {string}")
    fun searchBooksByTitle(title: String) {
        lastResponse = given()
            .`when`()
            .get("/books/title/$title")
            .then()
            .log().all()
            .statusCode(200)
    }

    @When("I delete the book with id created")
    fun deleteBook() {
        val book = lastResponse?.extract()?.`as`(BookDTO::class.java)

        given()
            .`when`()
            .delete("/books/${book?.id}")
            .then()
            .log().all()
            .statusCode(204)
    }

    @Then("the book with title {string} should not exist")
    fun verifyBookDoesNotExist(title: String) {
        given()
            .`when`()
            .get("/books/title/$title")
            .then()
            .log().all()
            .statusCode(404)
    }

    @When("I update the book with title {string} to have")
    fun updateBook(oldTitle: String, updates: Map<String, String>) {
        val response = given()
            .`when`()
            .get("/books/title/$oldTitle")
            .then()
            .extract()
            .body()
            .jsonPath()

        val books = response.getList<HashMap<String, Any>>("$")
        require(books.isNotEmpty()) { "No book found with title $oldTitle" }

        val bookId = (books.first()["id"] as Number).toLong()

        given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
            {
                ${updates.entries.joinToString { """"${it.key}": "${it.value}"""" }}
            }
        """.trimIndent()
            )
            .`when`()
            .patch("/books/$bookId")
            .then()
            .log().all()
            .statusCode(200)
    }
}
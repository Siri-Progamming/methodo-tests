Feature: Book REST API

  Scenario: Create and retrieve books
    Given I create a book with title "1984" and author "George Orwell"
    And I create a book with title "Brave New World" and author "Aldous Huxley"
    When I get all books
    Then the response should contain the following books
      | title           | author        |
      | 1984            | George Orwell |
      | Brave New World | Aldous Huxley |

  Scenario: Search books by title
    Given I create a book with title "The Hobbit" and author "J.R.R. Tolkien"
    When I search for books by title "The Hobbit"
    Then the response should contain the following books
      | title      | author         |
      | The Hobbit | J.R.R. Tolkien |

  Scenario: Delete a book
    Given I create a book with title "To Delete" and author "Test Author"
    When I delete the book with id created
    Then the book with title "To Delete" should not exist

  Scenario: Update a book
    Given I create a book with title "Old Title" and author "Original Author"
    When I update the book with title "Old Title" to have
      | title  | New Title      |
      | author | Updated Author |
    When I get all books
    Then the response should contain the following books
      | title     | author         |
      | New Title | Updated Author |
package kat.siri.test.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import kat.siri.test.dto.BookDTO

@Entity
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var title: String,
    var author: String,
){
    constructor() : this(null, "", "")

    init {
        require(title.isNotBlank()) { "Title cannot be empty or blank" }
        require(author.isNotBlank()) { "Author cannot be empty or blank" }
    }
    fun toDTO() = BookDTO(id ?: 0L, title, author)
}

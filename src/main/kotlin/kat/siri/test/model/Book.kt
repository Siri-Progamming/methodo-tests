package kat.siri.test.model

import jakarta.persistence.*
import kat.siri.test.dto.BookDTO
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Min(1)
    var id: Long? = null,

    @field:NotBlank(message = "Title cannot be blank")
    @field:NotEmpty
    @Column(nullable = false)
    var title: String,

    @field:NotBlank(message = "Author cannot be blank")
    @field:NotEmpty
    @Column(nullable = false)
    var author: String
) {
    constructor() : this(null, "", "")

    fun toDTO() = BookDTO(id ?: 0L, title, author)
}

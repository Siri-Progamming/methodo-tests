package kat.siri.test.dto

import kat.siri.test.model.Book

data class BookDTO(
    var id: Long? = null,
    var title: String,
    var author: String,
){
    fun toEntity() = Book(id ?: 0L, title, author)
}

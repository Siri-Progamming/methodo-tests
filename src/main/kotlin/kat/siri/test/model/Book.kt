package kat.siri.test.model

data class Book(
    var id: Long,
    var title: String,
    var author: String,
){
    init {
        require(title.isNotBlank()) { "Title cannot be empty or blank" }
        require(author.isNotBlank()) { "Author cannot be empty or blank" }
    }
}

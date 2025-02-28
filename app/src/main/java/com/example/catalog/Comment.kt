package com.example.catalog


//idInItem - id комментария в списке комментариев достоприм.
//imgId - id достоприм.
//username - login пользователя
//rating - рэтинг
//text - текст комментария
data class Comment(
    val idInItem: Int = 0,
    val imgId: Int = 0,
    val username: String = "",
    val rating: String = "",
    val text: String = ""
)

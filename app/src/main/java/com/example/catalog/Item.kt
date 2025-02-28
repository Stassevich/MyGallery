package com.example.catalog

//id - id достоприм
//image - список изображений
//title - название
//type - категория
//decs - краткое описание
//text - полное описание
data class Item(
    val id: Int = 0,
    val image: List<String> = emptyList(),
    val title: String = "",
    val type: String = "",
    val decs: String = "",
    val text: String = ""
)
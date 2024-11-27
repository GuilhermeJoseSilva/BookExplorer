// com/example/bookexplorer/api/VolumeInfo.kt
package com.example.bookexplorer.api

import com.example.bookexplorer.model.Book

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: ImageLinks?
)

fun VolumeInfo.toBook(): Book {
    return Book(
        title = this.title,
        author = this.authors?.joinToString(", ") ?: "Unknown",
        description = this.description ?: "No description available",
        thumbnail = this.imageLinks?.thumbnail ?: ""
    )
}

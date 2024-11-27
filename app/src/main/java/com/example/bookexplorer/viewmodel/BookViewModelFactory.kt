package com.example.bookexplorer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookexplorer.api.BookApiService
import com.example.bookexplorer.database.BookDao

class BookViewModelFactory(
    private val bookDao: BookDao,
    private val bookApiService: BookApiService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            return BookViewModel(bookDao, bookApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.bookexplorer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.bookexplorer.api.RetrofitInstance
import com.example.bookexplorer.database.BookDatabase
import com.example.bookexplorer.viewmodel.BookViewModel
import com.example.bookexplorer.viewmodel.BookViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar ViewModel manualmente
        val bookDao = BookDatabase.getDatabase(applicationContext).bookDao()
        val bookApiService = RetrofitInstance.api
        val factory = BookViewModelFactory(bookDao, bookApiService)
        viewModel = ViewModelProvider(this, factory)[BookViewModel::class.java]

        setContent {
            BookExplorerScreen(viewModel)
        }
    }
}

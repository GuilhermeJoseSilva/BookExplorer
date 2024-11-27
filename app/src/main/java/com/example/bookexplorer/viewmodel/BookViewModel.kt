package com.example.bookexplorer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookexplorer.api.BookApiService
import com.example.bookexplorer.database.BookDao
import com.example.bookexplorer.model.Book
import com.example.bookexplorer.api.toBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val bookDao: BookDao,
    private val bookApiService: BookApiService
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> get() = _books

    private val _searchResults = MutableStateFlow<List<Book>>(emptyList())
    val searchResults: StateFlow<List<Book>> get() = _searchResults

    // Carregar livros da base de dados local
    fun loadBooks() {
        viewModelScope.launch {
            _books.value = bookDao.getAllBooks()
        }
    }

    // Função para realizar a busca, local ou online
    fun searchBooks(query: String) {
        if (query.isNotEmpty()) {
            // Se houver algo no campo de busca, fazemos a busca online
            searchBooksOnline(query)
        } else {
            // Caso contrário, buscamos os livros locais
            searchBooksLocally("")
        }
    }

    // Buscar livros localmente no banco de dados
    fun searchBooksLocally(title: String) {
        viewModelScope.launch {
            _searchResults.value = bookDao.searchByTitle("%$title%")
        }
    }

    // Buscar livros online usando a API
    fun searchBooksOnline(query: String) {
        viewModelScope.launch {
            try {
                val response = bookApiService.searchBooks(query)
                if (response.items.isNotEmpty()) {
                    _searchResults.value = response.items.map { it.volumeInfo.toBook() }
                } else {
                    // Se não encontrar livros, podemos limpar os resultados ou fazer outro tratamento
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                // Em caso de erro, logamos para entender o que aconteceu
                Log.e("BookViewModel", "Erro ao buscar livros online", e)
                _searchResults.value = emptyList()
            }
        }
    }

    // Salvar livro no banco de dados
    fun saveBook(book: Book) {
        viewModelScope.launch {
            bookDao.insert(book)  // Insere o livro no banco de dados
            loadBooks() // Atualiza a lista de livros após salvar
        }
    }


    // Atualizar um livro no banco de dados
    fun updateBook(book: Book) {
        viewModelScope.launch {
            bookDao.update(book)
            loadBooks() // Atualizar a lista de livros após atualizar
        }
    }

    // Excluir livro do banco de dados
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            val rowsDeleted = bookDao.delete(book)
            Log.d("BookViewModel", "Rows deleted: $rowsDeleted")
            loadBooks() // Atualizar a lista de livros após exclusão
        }
    }


    // Inicializando a ViewModel e carregando livros locais
    init {
        loadBooks()
    }
}

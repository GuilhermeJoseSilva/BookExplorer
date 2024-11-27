package com.example.bookexplorer.database

import androidx.room.*
import com.example.bookexplorer.model.Book

@Dao
interface BookDao {
    // Insere um livro e retorna o ID gerado
    @Insert
    suspend fun insert(book: Book): Long  // Retorna o ID do livro inserido (Long)

    // Atualiza um livro e retorna o número de registros atualizados
    @Update
    suspend fun update(book: Book): Int  // Retorna o número de registros atualizados (Int)

    // Deleta um livro e retorna o número de registros deletados
    @Delete
    suspend fun delete(book: Book): Int  // Retorna o número de registros deletados (Int)

    // Obtém todos os livros e retorna uma lista de livros
    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<Book>  // Retorna uma lista de livros

    // Busca livros por título e retorna uma lista de livros
    @Query("SELECT * FROM books WHERE title LIKE :title")
    suspend fun searchByTitle(title: String): List<Book>  // Retorna uma lista de livros
}

package com.example.bookexplorer.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookexplorer.model.Book
import com.example.bookexplorer.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookExplorerScreen(viewModel: BookViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var bookTitle by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var bookDescription by remember { mutableStateOf("") }
    val books by viewModel.books.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text("Find books")

        // Barra de busca
        BasicTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchBooks(it.text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Formulário de criação
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextField(
                value = bookTitle,
                onValueChange = { bookTitle = it },
                label = { Text("Book Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.surface)
            )
            TextField(
                value = bookAuthor,
                onValueChange = { bookAuthor = it },
                label = { Text("Author") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.surface)
            )
            TextField(
                value = bookDescription,
                onValueChange = { bookDescription = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.surface)
            )
            Button(
                onClick = {
                    if (bookTitle.isNotEmpty() && bookAuthor.isNotEmpty()) {
                        val newBook = Book(
                            title = bookTitle,
                            author = bookAuthor,
                            description = bookDescription,
                            thumbnail = ""
                        )
                        viewModel.saveBook(newBook)
                        bookTitle = ""
                        bookAuthor = ""
                        bookDescription = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Add Book", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Livros
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val items = if (searchQuery.text.isEmpty()) books else searchResults

            items(items.size) { index ->
                val book = items[index]
                BookItem(
                    book = book,
                    onDeleteClick = { viewModel.deleteBook(it) },
                    onInsertClick = { viewModel.saveBook(it) },
                    onUpdateClick = { updatedBook ->
                        viewModel.updateBook(updatedBook)
                    }
                )
            }
        }
    }
}

@Composable
fun BookItem(
    book: Book,
    onInsertClick: (Book) -> Unit,
    onDeleteClick: (Book) -> Unit,
    onUpdateClick: (Book) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .shadow(8.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = "Author: ${book.author}", style = MaterialTheme.typography.bodyMedium)
                Text(text = book.description, style = MaterialTheme.typography.bodySmall)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { shareBookDetails(context, book) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Share", color = Color.White)
                }
                Button(
                    onClick = { onInsertClick(book) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Insert", color = Color.White)
                }
                Button(
                    onClick = { onDeleteClick(book) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White)
                }
                Button(
                    onClick = {
                        val updatedBook = book.copy(
                            title = "Updated ${book.title}",
                            author = "Updated ${book.author}",
                            description = "Updated ${book.description}"
                        )
                        onUpdateClick(updatedBook)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Update", color = Color.White)
                }
            }
        }
    }
}

fun shareBookDetails(context: android.content.Context, book: Book) {
    val shareText = """
        Check out this book!
        Title: ${book.title}
        Author: ${book.author}
        Description: ${book.description}
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

package max.keils.readlybook.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import max.keils.domain.usecase.DeleteBookUseCase
import max.keils.domain.usecase.GetUserBooksUseCase
import javax.inject.Inject

class BookListViewModel @Inject constructor(
    private val getUserBooksUseCase: GetUserBooksUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<BookListState> = MutableStateFlow(BookListState.Loading)
    val state
        get() = _state.asStateFlow()

    private val _search = MutableStateFlow("")
    val search
        get() = _search.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            getUserBooksUseCase(userId)
                .combine(search) { books, query ->
                    if (query.isBlank()) books
                    else books.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.author.contains(query, ignoreCase = true)

                    }
                }.collect { books ->
                    _state.value = when {
                        books.isEmpty() -> BookListState.Empty("No books found.")
                        else -> BookListState.Success(books)
                    }
                }
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            try {
                val success = deleteBookUseCase(bookId = bookId)
                _state.value = when (val currentState = _state.value) {
                    is BookListState.Success -> {
                        val updatedBooks = currentState.books.filterNot { it.id == bookId }
                        if (updatedBooks.isEmpty()) BookListState.Empty()
                        else BookListState.Success(updatedBooks)
                    }

                    else -> currentState
                }
            } catch (e: Exception) {
                _state.value = BookListState.Error("Failed to delete book: ${e.message}")
            }
        }
    }

    fun search(query: String) {
        _search.value = query
    }

}

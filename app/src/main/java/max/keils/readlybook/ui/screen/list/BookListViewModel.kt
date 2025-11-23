package max.keils.readlybook.ui.screen.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import max.keils.domain.usecase.DeleteBookEverywhereUseCase
import max.keils.domain.usecase.DeleteBookLocallyUseCase
import max.keils.domain.usecase.DownloadBookUseCase
import max.keils.domain.usecase.GetUserBooksFromCacheUseCase
import max.keils.domain.usecase.GetUserBooksUseCase
import max.keils.domain.usecase.SyncUserBooksWithFirebaseUseCase
import javax.inject.Inject

class BookListViewModel @Inject constructor(
    private val getUserBooksUseCase: GetUserBooksUseCase,
    private val getUserBooksFromCacheUseCase: GetUserBooksFromCacheUseCase,
    private val syncUserBooksWithFirebaseUseCase: SyncUserBooksWithFirebaseUseCase,
    private val deleteBookLocallyUseCase: DeleteBookLocallyUseCase,
    private val deleteBookEverywhereUseCase: DeleteBookEverywhereUseCase,
    private val downloadBookUseCase: DownloadBookUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<BookListState> = MutableStateFlow(BookListState.Loading)
    val state
        get() = _state.asStateFlow()

    private val _search = MutableStateFlow("")
    val search
        get() = _search.asStateFlow()

    private val _downloadingBooks = MutableStateFlow<Set<String>>(emptySet())
    val downloadingBooks
        get() = _downloadingBooks.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing
        get() = _isSyncing.asStateFlow()

    private val _deleteDialogState = MutableStateFlow<DeleteDialogState?>(null)
    val deleteDialogState
        get() = _deleteDialogState.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            val cachedBooks = getUserBooksFromCacheUseCase(userId)
            if (cachedBooks == null) {
                syncWithFirebase(userId)
            }

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

    fun syncWithFirebase(userId: String) {
        viewModelScope.launch {
            try {
                _isSyncing.value = true
                val books = syncUserBooksWithFirebaseUseCase(userId)
                _isSyncing.value = false
            } catch (e: Exception) {
                _isSyncing.value = false
                _state.value = BookListState.Error("Failed to sync: ${e.message}")
            }
        }
    }

    fun showDeleteDialog(bookId: String, bookTitle: String) {
        _deleteDialogState.value = DeleteDialogState(bookId, bookTitle)
    }

    fun dismissDeleteDialog() {
        _deleteDialogState.value = null
    }

    fun deleteBookLocally(bookId: String) {
        viewModelScope.launch {
            try {
                deleteBookLocallyUseCase(bookId)
            } catch (e: Exception) {
                _state.value = BookListState.Error("Failed to delete book: ${e.message}")
            }
        }
    }

    fun deleteBookEverywhere(bookId: String) {
        viewModelScope.launch {
            try {
                deleteBookEverywhereUseCase(bookId)
            } catch (e: Exception) {
                _state.value = BookListState.Error("Failed to delete book: ${e.message}")
            }
        }
    }

    fun search(query: String) {
        _search.value = query
    }

    fun downloadBook(bookId: String) {
        viewModelScope.launch {
            try {
                _downloadingBooks.value += bookId

                val downloadedBook = downloadBookUseCase(bookId)

                _downloadingBooks.value -= bookId

                _state.value = when (val currentState = _state.value) {
                    is BookListState.Success -> {
                        val updatedBooks = currentState.books.map { book ->
                            if (book.id == bookId) downloadedBook else book
                        }
                        BookListState.Success(updatedBooks)
                    }
                    else -> currentState
                }
            } catch (e: Exception) {
                _downloadingBooks.value -= bookId
                _state.value = BookListState.Error("Failed to download book: ${e.message}")
            }
        }
    }

}


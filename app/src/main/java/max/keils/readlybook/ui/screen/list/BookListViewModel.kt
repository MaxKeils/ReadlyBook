package max.keils.readlybook.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import max.keils.domain.usecase.GetUserBooksUseCase
import javax.inject.Inject

class BookListViewModel @Inject constructor(
    private val getUserBooksUseCase: GetUserBooksUseCase
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

    fun search(query: String) {
        _search.value = query
    }

}

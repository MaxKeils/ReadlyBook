package max.keils.readlybook.ui.screen.list

import max.keils.domain.entity.Book

sealed class BookListState {

    object Loading : BookListState()

    data class Success(val books: List<Book>) : BookListState()

    data class Empty(val message: String = "There's nothing here ;)") : BookListState()

    data class Error(val error: String) : BookListState()

}
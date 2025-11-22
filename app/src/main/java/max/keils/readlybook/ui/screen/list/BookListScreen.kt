package max.keils.readlybook.ui.screen.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import max.keils.domain.entity.Book
import max.keils.readlybook.R
import max.keils.readlybook.ui.components.BookListItem
import max.keils.readlybook.ui.components.ReadlySearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    userId: String,
    rootPaddingValues: PaddingValues,
    viewModel: BookListViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    val searchBarState = rememberSearchBarState()
    val searchQuery by viewModel.search.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = stringResource(R.string.my_books))
            })
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ReadlySearchBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                searchBarState = searchBarState,
                searchQuery = searchQuery,
                onSearchQueryChange = { newQuery ->
                    viewModel.search(newQuery)
                }
            )
            when (val state = state) {
                BookListState.Loading -> {
                    CircularProgressIndicator()
                }

                is BookListState.Success -> {
                    val books = state.books
                    BookListContent(
                        books = books,
                        rootPaddingValues = rootPaddingValues,
                        onBookClick = {

                        },
                        onDownloadClick = {

                        },
                        onDeleteClick = { viewModel.deleteBook(it.id) }
                    )
                }

                is BookListState.Empty -> {

                }

                is BookListState.Error -> {

                }
            }
        }
    }


}

@Composable
private fun BookListContent(
    books: List<Book>,
    rootPaddingValues: PaddingValues,
    onBookClick: (Book) -> Unit,
    onDownloadClick: (Book) -> Unit,
    onDeleteClick: (Book) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(
            bottom = rootPaddingValues.calculateBottomPadding(),
            top = 8.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = books, key = { it.id }) {
            BookListItem(
                book = it,
                onDeleteClick = { onDeleteClick(it) },
                onDownloadClick = { onDownloadClick(it) },
                onBookClick = { onBookClick(it) }
            )
        }
    }
}
package max.keils.readlybook.ui.screen.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import max.keils.domain.entity.Book
import max.keils.readlybook.R
import max.keils.readlybook.ui.components.BookListItem
import max.keils.readlybook.ui.components.DeleteConfirmDialog
import max.keils.readlybook.ui.components.LottieAnimation
import max.keils.readlybook.ui.components.ReadlySearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    userId: String,
    rootPaddingValues: PaddingValues,
    viewModel: BookListViewModel = viewModel(),
    onBookClick: (Book) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val downloadingBooks by viewModel.downloadingBooks.collectAsState()

    val searchBarState = rememberSearchBarState()
    val searchQuery by viewModel.search.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load(userId)
    }

    val isSyncing by viewModel.isSyncing.collectAsState()
    val deleteDialogState by viewModel.deleteDialogState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.my_books))
                },
                actions = {
                    androidx.compose.material3.IconButton(
                        onClick = { viewModel.syncWithFirebase(userId) },
                        enabled = !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(12.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Sync,
                                contentDescription = "Sync with Firebase"
                            )
                        }
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                        downloadingBooks = downloadingBooks,
                        rootPaddingValues = rootPaddingValues,
                        onBookClick = onBookClick,
                        onDownloadClick = { book ->
                            viewModel.downloadBook(book.id)
                        },
                        onDeleteClick = { book ->
                            viewModel.showDeleteDialog(book.id, book.title)
                        }
                    )
                }

                is BookListState.Empty -> {
                    LottieAnimation(rawRes = R.raw.empty_book_list)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.oops_error, state.message))
                }

                is BookListState.Error -> {
                    LottieAnimation(rawRes = R.raw.failed_upload)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.oops_error, state.error))
                }
            }
        }
    }

    deleteDialogState?.let { dialogState ->
        DeleteConfirmDialog(
            bookTitle = dialogState.bookTitle,
            onDismiss = { viewModel.dismissDeleteDialog() },
            onDeleteLocal = {
                viewModel.deleteBookLocally(dialogState.bookId)
                viewModel.dismissDeleteDialog()
            },
            onDeleteLocalAndServer = {
                viewModel.deleteBookEverywhere(dialogState.bookId)
                viewModel.dismissDeleteDialog()
            }
        )
    }
}

@Composable
private fun BookListContent(
    books: List<Book>,
    downloadingBooks: Set<String>,
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
                isDownloading = downloadingBooks.contains(it.id),
                onDeleteClick = { onDeleteClick(it) },
                onDownloadClick = { onDownloadClick(it) },
                onBookClick = { onBookClick(it) }
            )
        }
    }
}
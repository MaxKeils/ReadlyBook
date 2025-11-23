package max.keils.readlybook.ui.screen.main

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import max.keils.readlybook.R
import max.keils.readlybook.di.ViewModelFactory
import max.keils.readlybook.ui.components.NavigationBar
import max.keils.readlybook.ui.navigation.AppNavGraph
import max.keils.readlybook.ui.navigation.NavigationItem
import max.keils.readlybook.ui.navigation.rememberNavigationState
import max.keils.readlybook.ui.screen.list.BookListScreen
import max.keils.readlybook.ui.screen.profile.ProfileScreen
import max.keils.readlybook.ui.screen.reader.ReaderScreen
import max.keils.readlybook.ui.screen.upload.UploadBookScreen

@Composable
fun MainScreen(viewModelFactory: ViewModelFactory, userId: String?) {
    val navigationState = rememberNavigationState()

    val navigationItems = listOf(
        NavigationItem.BookList,
        NavigationItem.BookLoading,
        NavigationItem.Profile
    )

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                items = navigationItems,
                navigationState = navigationState
            )
        }
    ) { paddingValues ->
        AppNavGraph(
            navHostController = navigationState.navHostController,
            bookListScreen = {
                userId?.let { userId ->
                    BookListScreen(
                        userId = userId,
                        viewModel = viewModel(factory = viewModelFactory),
                        rootPaddingValues = paddingValues,
                        onBookClick = { book ->
                            if (book.isAvailableOffline) {
                                navigationState.navHostController.navigate(
                                    max.keils.readlybook.ui.navigation.Screen.ReaderScreen(
                                        bookId = book.id,
                                        bookTitle = book.title,
                                        userId = userId
                                    )
                                )
                            } else Toast.makeText(
                                context,
                                context.getString(R.string.the_book_is_not_available_download_it),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            },
            uploadBookScreen = { UploadBookScreen(viewModel = viewModel(factory = viewModelFactory)) },
            profileScreen = {
                ProfileScreen(
                    viewModel = viewModel(factory = viewModelFactory)
                )
            },
            readerScreen = { bookId, bookTitle, readerUserId ->
                ReaderScreen(
                    bookId = bookId,
                    bookTitle = bookTitle,
                    userId = readerUserId,
                    viewModel = viewModel(factory = viewModelFactory),
                    onNavigateBack = { navigationState.navHostController.popBackStack() }
                )
            },
            startDestination = NavigationItem.BookList.screen,
        )
    }
}
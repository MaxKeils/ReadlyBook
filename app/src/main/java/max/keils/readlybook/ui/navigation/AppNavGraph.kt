package max.keils.readlybook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
internal fun AppNavGraph(
    navHostController: NavHostController,
    bookListScreen: @Composable () -> Unit,
    uploadBookScreen: @Composable () -> Unit,
    profileScreen: @Composable () -> Unit,
    readerScreen: @Composable (String, String, String) -> Unit,
    startDestination: Screen = Screen.BookList,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Screen.BookList> { bookListScreen() }
        composable<Screen.BookLoading> { uploadBookScreen() }
        composable<Screen.Profile> { profileScreen() }
        composable<Screen.ReaderScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ReaderScreen>()
            readerScreen(args.bookId, args.bookTitle, args.userId)
        }
    }
}
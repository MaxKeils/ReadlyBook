package max.keils.readlybook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
internal fun AppNavGraph(
    navHostController: NavHostController,
    bookListScreen: @Composable () -> Unit,
    uploadBookScreen: @Composable () -> Unit,
    profileScreen: @Composable () -> Unit,
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
    }
}
package max.keils.readlybook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
internal fun RootNavGraph(
    navHostController: NavHostController,
    authScreen: @Composable () -> Unit,
    mainScreen: @Composable () -> Unit,
    startDestination: Screen = Screen.Auth
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable<Screen.Auth> { authScreen() }
        composable<Screen.Main> { mainScreen() }
    }

}
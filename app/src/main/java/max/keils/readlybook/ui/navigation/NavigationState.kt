package max.keils.readlybook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

internal class NavigationState(
    val navHostController: NavHostController
) {

    fun navigateTo(route: Screen) {
        navHostController.navigate(route = route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(
                route = navHostController.graph.safeStartDestinationRoute,
                popUpToBuilder = {
                    saveState = true
                })
        }
    }
}

@Composable
internal fun rememberNavigationState(
    navHostController: NavHostController = rememberNavController()
): NavigationState = remember {
    NavigationState(navHostController)
}

val NavGraph.safeStartDestinationRoute: String
    get() = startDestinationRoute ?: error("Start destination route is null")
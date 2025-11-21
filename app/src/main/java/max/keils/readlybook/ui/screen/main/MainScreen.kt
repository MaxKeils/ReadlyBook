package max.keils.readlybook.ui.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import max.keils.readlybook.di.ViewModelFactory
import max.keils.readlybook.ui.components.NavigationBar
import max.keils.readlybook.ui.navigation.AppNavGraph
import max.keils.readlybook.ui.navigation.NavigationItem
import max.keils.readlybook.ui.navigation.rememberNavigationState

@Composable
fun MainScreen(viewModelFactory: ViewModelFactory) {
    val navigationState = rememberNavigationState()

    val navigationItems = listOf(
        NavigationItem.BookList,
        NavigationItem.BookLoading,
        NavigationItem.Profile
    )

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
            bookListScreen = { Text("Book list screen") },
            uploadBookScreen = { Text("Upload book") },
            profileScreen = { Text("Profile screen") },
            startDestination = NavigationItem.BookList.screen,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
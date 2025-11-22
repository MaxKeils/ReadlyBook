package max.keils.readlybook.ui.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import max.keils.readlybook.di.ViewModelFactory
import max.keils.readlybook.ui.components.NavigationBar
import max.keils.readlybook.ui.navigation.AppNavGraph
import max.keils.readlybook.ui.navigation.NavigationItem
import max.keils.readlybook.ui.navigation.rememberNavigationState
import max.keils.readlybook.ui.screen.list.BookListScreen
import max.keils.readlybook.ui.screen.upload.UploadBookScreen

@Composable
fun MainScreen(viewModelFactory: ViewModelFactory, userId: String?) {
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
            bookListScreen = {
                userId?.let { userId ->
                    BookListScreen(userId, viewModel = viewModel(factory = viewModelFactory), rootPaddingValues = paddingValues)
                }
            },
            uploadBookScreen = { UploadBookScreen(viewModel = viewModel(factory = viewModelFactory)) },
            profileScreen = { Text("Profile screen") },
            startDestination = NavigationItem.BookList.screen,
        )
    }
}
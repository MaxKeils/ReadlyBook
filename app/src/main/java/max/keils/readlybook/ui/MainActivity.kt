package max.keils.readlybook.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import max.keils.domain.usecase.GetCurrentUserIdUseCase
import max.keils.readlybook.appComponent
import max.keils.readlybook.di.ViewModelFactory
import max.keils.readlybook.ui.navigation.RootNavGraph
import max.keils.readlybook.ui.navigation.Screen
import max.keils.readlybook.ui.screen.auth.AuthScreenRoute
import max.keils.readlybook.ui.screen.auth.AuthViewModel
import max.keils.readlybook.ui.screen.main.MainScreen
import max.keils.readlybook.ui.theme.ReadlyBookTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadlyBookTheme {
                val navHostController = rememberNavController()
                AppContent(navHostController = navHostController)
            }
        }
    }

    @Composable
    private fun AppContent(navHostController: NavHostController) {
        val viewModel: AuthViewModel = viewModel(factory = viewModelFactory)
        val currentUserId by getCurrentUserIdUseCase().collectAsState()

        Log.d("ReadlyApp", "Current User ID: $currentUserId")

        RootNavGraph(
            navHostController = navHostController,
            startDestination = if (currentUserId == null) Screen.Auth else Screen.Main,
            authScreen = {
                AuthScreenRoute(viewModel)
            },
            mainScreen = {
                MainScreen(viewModelFactory)
            }
        )

    }

}
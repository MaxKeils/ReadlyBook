package max.keils.readlybook.ui.screen.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun AuthScreenRoute(
    viewModel: AuthViewModel
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(AuthTab.Login) }

    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            Toast.makeText(context, "Authentication Successful", Toast.LENGTH_SHORT)
                .show()
        }
    }

    Log.d(
        "ReadlyApp",
        "AuthScreenRoute recomposed with state: $state and selectedTab: $selectedTab"
    )

    AuthScreen(
        state = state,
        selectedTab = selectedTab,
        onTabChange = { selectedTab = it },
        onLoginClick = { email, password ->
            viewModel.signIn(email, password)
        },
        onRegisterClick = { email, password, repeatPassword ->
            viewModel.signUp(email, password, repeatPassword)
        }
    )
}
package max.keils.readlybook.ui.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import max.keils.domain.entity.UserData
import max.keils.domain.usecase.SignInUseCase
import max.keils.domain.usecase.SignUpUseCase
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state
        get() = _state.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthState.Error("Email and password must not be empty")
            return
        }

        if (!isValidEmail(email)) {
            _state.value = AuthState.Error("Invalid email format")
            return
        }

        performAuth { signInUseCase(email, password) }
    }

    fun signUp(email: String, password: String, repeatPassword: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthState.Error("Email and password must not be empty")
            return
        }

        if (!isValidEmail(email)) {
            _state.value = AuthState.Error("Invalid email format")
            return
        }

        if (password != repeatPassword) {
            _state.value = AuthState.Error("Passwords do not match")
            return
        }

        performAuth { signUpUseCase(email, password) }
    }

    private fun performAuth(
        block: suspend () -> Result<UserData>
    ) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            block().fold(
                onSuccess = { _state.value = AuthState.Success(it) },
                onFailure = {
                    Log.d("AuthViewModel", "performAuth failed:", it)
                    val message = when (it) {
                        is java.net.UnknownHostException -> "No internet connection"
                        else -> it.message ?: "Unknown error"
                    }
                    _state.value = AuthState.Error(message)
                }
            )
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return emailRegex.matches(email)
    }
}
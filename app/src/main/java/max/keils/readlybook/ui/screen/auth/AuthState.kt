package max.keils.readlybook.ui.screen.auth

import max.keils.domain.entity.UserData

sealed class AuthState {

    data object Idle : AuthState()

    data object Loading : AuthState()

    data class Success(val user: UserData) : AuthState()

    data class Error(val message: String) : AuthState()

}
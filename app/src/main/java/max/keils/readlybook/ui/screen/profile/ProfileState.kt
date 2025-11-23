package max.keils.readlybook.ui.screen.profile

import max.keils.domain.entity.UserData

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Content(
        val user: UserData,
        val isEditing: Boolean = false,
        val editedName: String = user.name ?: "",
        val isUploading: Boolean = false
    ) : ProfileState()
    data class Error(val message: String) : ProfileState()
}


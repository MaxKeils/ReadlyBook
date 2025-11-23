package max.keils.readlybook.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import max.keils.domain.usecase.GetCurrentUserIdUseCase
import max.keils.domain.usecase.GetCurrentUserUseCase
import max.keils.domain.usecase.SignOutUseCase
import max.keils.domain.usecase.UpdateUserProfileUseCase
import max.keils.domain.usecase.UploadUserPhotoUseCase
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadUserPhotoUseCase: UploadUserPhotoUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state = _state.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            val result = getCurrentUserUseCase()
            result.fold(
                onSuccess = { userData ->
                    if (userData != null) {
                        _state.value = ProfileState.Content(user = userData)
                    } else {
                        _state.value = ProfileState.Error("User not found")
                    }
                },
                onFailure = { error ->
                    _state.value =
                        ProfileState.Error("Profile upload error: ${error.localizedMessage}")
                }
            )
        }
    }

    fun startEditing() {
        val currentState = _state.value
        if (currentState is ProfileState.Content) {
            _state.value = currentState.copy(isEditing = true)
        }
    }

    fun cancelEditing() {
        val currentState = _state.value
        if (currentState is ProfileState.Content) {
            _state.value = currentState.copy(
                isEditing = false,
                editedName = currentState.user.name ?: ""
            )
        }
    }

    fun updateEditedName(name: String) {
        val currentState = _state.value
        if (currentState is ProfileState.Content) {
            _state.value = currentState.copy(editedName = name)
        }
    }

    fun saveProfile() {
        val currentState = _state.value
        if (currentState !is ProfileState.Content) return

        viewModelScope.launch {
            _state.value = currentState.copy(isUploading = true)

            val newName = currentState.editedName.takeIf { it.isNotBlank() }

            updateUserProfileUseCase(
                displayName = newName,
                photoUrl = null
            ).fold(
                onSuccess = { updatedUser ->
                    _state.value = ProfileState.Content(
                        user = updatedUser,
                        isEditing = false,
                        editedName = updatedUser.name ?: ""
                    )
                },
                onFailure = { error ->
                    _state.value = currentState.copy(isUploading = false)
                    _state.value =
                        ProfileState.Error("Profile update error: ${error.localizedMessage}")
                }
            )
        }
    }

    fun uploadPhoto(photoBytes: ByteArray) {
        val currentState = _state.value
        if (currentState !is ProfileState.Content) return

        viewModelScope.launch {
            _state.value = currentState.copy(isUploading = true)

            val userId = getCurrentUserIdUseCase().value ?: run {
                _state.value = currentState.copy(isUploading = false)
                _state.value = ProfileState.Error("The user's ID was not found")
                return@launch
            }

            uploadUserPhotoUseCase(photoBytes, userId).fold(
                onSuccess = { photoUrl ->
                    updateUserProfileUseCase(
                        displayName = null,
                        photoUrl = photoUrl
                    ).fold(
                        onSuccess = { updatedUser ->
                            _state.value = ProfileState.Content(
                                user = updatedUser,
                                isEditing = currentState.isEditing,
                                editedName = currentState.editedName
                            )
                        },
                        onFailure = { error ->
                            _state.value = currentState.copy(isUploading = false)
                            _state.value =
                                ProfileState.Error("Photo update error: ${error.localizedMessage}")
                        }
                    )
                },
                onFailure = { error ->
                    _state.value = currentState.copy(isUploading = false)
                    _state.value =
                        ProfileState.Error("Error uploading photo: ${error.localizedMessage}")
                }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase().fold(
                onSuccess = { },
                onFailure = { error ->
                    val currentState = _state.value
                    if (currentState is ProfileState.Content) {
                        _state.value = ProfileState.Error("Exit error: ${error.localizedMessage}")
                    }
                }
            )
        }
    }
}


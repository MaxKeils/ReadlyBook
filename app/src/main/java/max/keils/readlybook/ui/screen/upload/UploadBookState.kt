package max.keils.readlybook.ui.screen.upload

sealed class UploadBookState {

    data object Idle : UploadBookState()

    data class Uploading(val progress: Float) : UploadBookState()

    data object Success : UploadBookState()

    data class Error(val message: String) : UploadBookState()

}
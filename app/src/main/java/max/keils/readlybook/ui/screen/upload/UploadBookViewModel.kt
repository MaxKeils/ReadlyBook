package max.keils.readlybook.ui.screen.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import max.keils.data.source.local.BookCacheManager
import max.keils.data.worker.UploadBookWorker
import max.keils.domain.usecase.GetCurrentUserIdUseCase
import javax.inject.Inject

class UploadBookViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val bookCacheManager: BookCacheManager
) : ViewModel() {

    private val _state = MutableStateFlow<UploadBookState>(UploadBookState.Idle)
    val state
        get() = _state.asStateFlow()

    fun resetState() {
        _state.value = UploadBookState.Idle
    }

    fun uploadBookInBackground(
        context: Context,
        fileUri: Uri,
        title: String,
        author: String,
        coverUrl: String? = null
    ) {
        if (title.isEmpty()) {
            _state.value = UploadBookState.Error("Title cannot be empty")
            return
        }

        if (author.isEmpty()) {
            _state.value = UploadBookState.Error("Author cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = UploadBookState.Uploading(0f)

                val fileName = bookCacheManager.getFileNameFromUri(fileUri) ?: ""
                val userId = getCurrentUserIdUseCase().first() ?: run {
                    _state.value = UploadBookState.Error("User not logged in")
                    return@launch
                }

                val cachedFile = bookCacheManager.copyUriToTempCache(fileUri, fileName)

                val inputDataBuilder = mutableMapOf(
                    "filePath" to cachedFile.absolutePath,
                    "fileName" to fileName,
                    "title" to title,
                    "author" to author,
                    "userId" to userId
                )

                coverUrl?.let { inputDataBuilder["coverUrl"] = it }

                val request = OneTimeWorkRequestBuilder<UploadBookWorker>()
                    .setInputData(workDataOf(*inputDataBuilder.toList().toTypedArray()))
                    .build()

                val workManager = WorkManager.getInstance(context)
                workManager.enqueue(request)

                workManager.getWorkInfoByIdLiveData(request.id).observeForever { workInfo ->
                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.RUNNING -> {
                                val progress = workInfo.progress.getFloat("progress", 0f)
                                _state.value = UploadBookState.Uploading(progress)
                            }

                            WorkInfo.State.SUCCEEDED -> {
                                _state.value = UploadBookState.Success
                            }

                            WorkInfo.State.FAILED -> {
                                val error =
                                    workInfo.outputData.getString("error") ?: "Upload failed"
                                _state.value = UploadBookState.Error(error)
                            }

                            WorkInfo.State.CANCELLED -> {
                                _state.value = UploadBookState.Error("Upload cancelled")
                            }

                            else -> {}
                        }
                    }
                }

            } catch (e: Exception) {
                _state.value = UploadBookState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getFileName(uri: Uri): String {
        return bookCacheManager.getFileNameFromUri(uri) ?: "unknown"
    }

}
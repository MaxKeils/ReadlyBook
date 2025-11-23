package max.keils.readlybook.ui.screen.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import max.keils.data.source.local.BookCacheManager
import max.keils.data.worker.UploadBookWorker
import max.keils.domain.usecase.GetCurrentUserIdUseCase
import java.util.UUID
import javax.inject.Inject

class UploadBookViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val bookCacheManager: BookCacheManager
) : ViewModel() {

    private val _state = MutableStateFlow<UploadBookState>(UploadBookState.Idle)
    val state
        get() = _state.asStateFlow()

    private var currentWorkManager: WorkManager? = null
    private var currentWorkId: UUID? = null
    private var enqueuedTimeoutJob: Job? = null
    private var workFlowJob: Job? = null

    fun resetState() {
        enqueuedTimeoutJob?.cancel()
        workFlowJob?.cancel()
        enqueuedTimeoutJob = null
        workFlowJob = null
        currentWorkId = null
        currentWorkManager = null
        _state.value = UploadBookState.Idle
    }

    fun cancelUpload() {
        currentWorkId?.let { workId ->
            currentWorkManager?.cancelWorkById(workId)
            _state.value = UploadBookState.Idle
            android.util.Log.d("ReadlyApp", "UploadBookViewModel: Upload cancelled by user")
        }
        resetState()
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
                currentWorkManager = workManager
                currentWorkId = request.id

                workManager.enqueue(request)

                workFlowJob?.cancel()
                workFlowJob = workManager.getWorkInfoByIdFlow(request.id)
                    .onEach { workInfo ->
                        workInfo?.let {
                            handleWorkState(it)
                        }
                    }
                    .launchIn(viewModelScope)

            } catch (e: Exception) {
                _state.value = UploadBookState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun handleWorkState(workInfo: WorkInfo) {
        when (workInfo.state) {
            WorkInfo.State.ENQUEUED -> {
                _state.value = UploadBookState.Uploading(0f)
                startEnqueuedTimeout(workInfo.id)
            }

            WorkInfo.State.RUNNING -> {
                enqueuedTimeoutJob?.cancel()
                val progress = workInfo.progress.getFloat("progress", 0f)
                _state.value = UploadBookState.Uploading(progress)
            }

            WorkInfo.State.SUCCEEDED -> {
                enqueuedTimeoutJob?.cancel()
                _state.value = UploadBookState.Success
            }

            WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                enqueuedTimeoutJob?.cancel()
                _state.value = UploadBookState.Error("Upload failed")
            }

            else -> Unit
        }
    }

    private fun startEnqueuedTimeout(workId: UUID) {
        enqueuedTimeoutJob?.cancel()
        enqueuedTimeoutJob = viewModelScope.launch {
            delay(10_000)

            val info = withTimeoutOrNull(2000) {
                currentWorkManager?.getWorkInfoByIdFlow(workId)?.first()
            }

            if (info?.state == WorkInfo.State.ENQUEUED) {
                currentWorkManager?.cancelWorkById(workId)
                _state.value = UploadBookState.Error("Upload timeout. Please check your internet.")
            }
        }
    }

    fun getFileName(uri: Uri): String {
        return bookCacheManager.getFileNameFromUri(uri) ?: "unknown"
    }
}
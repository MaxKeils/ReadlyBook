package max.keils.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import max.keils.data.source.local.BookCacheManager
import max.keils.domain.repository.BookRepository
import max.keils.domain.usecase.UploadBookUseCase

class UploadBookWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val uploadBookUseCase: UploadBookUseCase,
    private val cacheManager: BookCacheManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val fileName = inputData.getString("fileName") ?: return Result.failure()
            val filePath = inputData.getString("filePath") ?: return Result.failure()
            val title = inputData.getString("title") ?: return Result.failure()
            val author = inputData.getString("author") ?: return Result.failure()

            val file = java.io.File(filePath)
            if (!file.exists()) {
                return Result.failure(
                    workDataOf("error" to "File not found")
                )
            }

            val fileBytes = file.readBytes()

            val book = max.keils.domain.entity.Book(
                id = "",
                title = title,
                author = author,
                fileUrl = "",
                userId = inputData.getString("userId") ?: "",
                fileName = fileName,
                localPath = null,
                uploadedAt = System.currentTimeMillis()
            )

            val bookId = uploadBookUseCase(book, fileBytes) { progress ->
                setProgress(workDataOf("progress" to progress))
            }

            try {
                cacheManager.saveBookToCache(bookId, fileBytes, fileName)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to cache book locally", e)
            }

            try {
                file.delete()
            } catch (e: Exception) {
                Log.w("ReadlyApp", "Failed to delete temporary file", e)
            }

            Result.success()

        } catch (exception: Exception) {
            Result.failure(
                workDataOf("error" to (exception.message ?: "Unknown error"))
            )
        }
    }

    companion object {
        private const val TAG = "UploadBookWorker"
    }
}
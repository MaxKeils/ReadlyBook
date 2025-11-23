package max.keils.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import max.keils.data.source.local.BookCacheManager
import max.keils.domain.usecase.UploadBookUseCase

class UploadBookWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val uploadBookUseCase: UploadBookUseCase,
    private val cacheManager: BookCacheManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "doWork: Starting upload")

            // Проверка наличия интернета
            if (!isNetworkAvailable()) {
                Log.e(TAG, "doWork: No internet connection")
                return Result.failure(
                    workDataOf("error" to "No internet connection")
                )
            }

            val fileName = inputData.getString("fileName") ?: return Result.failure()
            val filePath = inputData.getString("filePath") ?: return Result.failure()
            val title = inputData.getString("title") ?: return Result.failure()
            val author = inputData.getString("author") ?: return Result.failure()
            val coverUrl = inputData.getString("coverUrl")

            val file = java.io.File(filePath)
            if (!file.exists()) {
                Log.e(TAG, "doWork: File not found at $filePath")
                return Result.failure(
                    workDataOf("error" to "File not found")
                )
            }

            val fileBytes = file.readBytes()
            Log.d(TAG, "doWork: File read, size=${fileBytes.size} bytes")

            val book = max.keils.domain.entity.Book(
                id = "",
                title = title,
                author = author,
                fileUrl = "",
                userId = inputData.getString("userId") ?: "",
                fileName = fileName,
                localPath = null,
                coverUrl = coverUrl,
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
                Log.w(TAG, "Failed to delete temporary file", e)
            }

            Log.d(TAG, "doWork: Upload completed successfully")
            Result.success()

        } catch (exception: Exception) {
            Log.e(TAG, "doWork: Upload failed", exception)

            val errorMessage = when {
                exception.message?.contains("timeout", ignoreCase = true) == true ->
                    "Upload timeout. Please check your internet connection"
                exception.message?.contains("network", ignoreCase = true) == true ->
                    "Network error. Please check your internet connection"
                exception.message?.contains("unable to resolve host", ignoreCase = true) == true ->
                    "No internet connection"
                else -> exception.message ?: "Upload failed"
            }

            Result.failure(
                workDataOf("error" to errorMessage)
            )
        }
    }

    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? android.net.ConnectivityManager
            val network = connectivityManager?.activeNetwork
            val capabilities = connectivityManager?.getNetworkCapabilities(network)
            capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } catch (e: Exception) {
            Log.e(TAG, "isNetworkAvailable: Error checking network", e)
            false
        }
    }

    companion object {
        private const val TAG = "UploadBookWorker"
    }
}
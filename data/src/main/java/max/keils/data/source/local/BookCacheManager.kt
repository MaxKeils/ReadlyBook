package max.keils.data.source.local

import android.app.Application
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookCacheManager @Inject constructor(
    private val context: Application
) {

    private val persistentDir: File
        get() = File(context.filesDir, "downloaded_books").apply {
            if (!exists()) mkdirs()
        }

    private val tempUploadDir: File
        get() = File(context.cacheDir, "upload_temp").apply {
            if (!exists()) mkdirs()
        }

    suspend fun saveBookToCache(
        bookId: String,
        fileBytes: ByteArray,
        fileName: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val bookDir = File(persistentDir, bookId).apply {
                if (!exists()) mkdirs()
            }
            val file = File(bookDir, fileName)
            file.writeBytes(fileBytes)
            file.absolutePath
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getBookFromCache(bookId: String): File? = withContext(Dispatchers.IO) {
        try {
            val bookDir = File(persistentDir, bookId)
            if (!bookDir.exists()) return@withContext null

            val files = bookDir.listFiles()
            files?.firstOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get book from cache", e)
            null
        }
    }

    suspend fun isBookCached(bookId: String): Boolean = withContext(Dispatchers.IO) {
        val bookDir = File(persistentDir, bookId)
        bookDir.exists() && bookDir.listFiles()?.isNotEmpty() == true
    }

    suspend fun removeBookFromCache(bookId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val bookDir = File(persistentDir, bookId)
            val deleted = bookDir.deleteRecursively()
            deleted
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearCache(): Boolean = withContext(Dispatchers.IO) {
        try {
            val deleted = persistentDir.deleteRecursively()
            persistentDir.mkdirs()
            Log.d(TAG, "Cache cleared")
            deleted
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear cache", e)
            false
        }
    }

    suspend fun getCachedBooks(): List<String> = withContext(Dispatchers.IO) {
        try {
            persistentDir.listFiles()
                ?.filter { it.isDirectory }
                ?.map { it.name }
                ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get cached books", e)
            emptyList()
        }
    }


    fun getFileNameFromUri(uri: android.net.Uri): String? {
        var fileName: String? = null

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex =
                        cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get file name from Uri", e)
        }

        return fileName ?: uri.lastPathSegment
    }


    suspend fun copyUriToTempCache(uri: android.net.Uri, fileName: String): File =
        withContext(Dispatchers.IO) {
            try {
                val cachedFile = File(tempUploadDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    cachedFile.outputStream().use { output ->
                        val bytesWritten = input.copyTo(output)
                        Log.d(TAG, "File copied to temp cache: $fileName ($bytesWritten bytes)")
                    }
                } ?: throw IllegalStateException("Cannot open input stream for Uri: $uri")

                cachedFile
            } catch (e: Exception) {
                Log.e(TAG, "Failed to copy Uri to temp cache", e)
                throw e
            }
        }

    suspend fun clearTempUploadCache(): Boolean = withContext(Dispatchers.IO) {
        try {
            val deleted = tempUploadDir.deleteRecursively()
            if (deleted) {
                tempUploadDir.mkdirs()
                Log.d(TAG, "Temp upload cache cleared")
            }
            deleted
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear temp upload cache", e)
            false
        }
    }

    companion object {
        private const val TAG = "BookCacheManager"
    }
}
package max.keils.data.source.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import max.keils.domain.entity.ReadingProgress
import javax.inject.Inject

class ReadingProgressDataSource @Inject constructor(
    context: Application
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getProgress(bookId: String): ReadingProgress? {
        val position = prefs.getInt(getKey(bookId, KEY_POSITION), -1)
        if (position == -1) return null

        return ReadingProgress(
            bookId = bookId,
            currentPosition = position,
            totalSize = prefs.getInt(getKey(bookId, KEY_TOTAL_SIZE), 0),
            currentPage = prefs.getInt(getKey(bookId, KEY_CURRENT_PAGE), 0),
            totalPages = prefs.getInt(getKey(bookId, KEY_TOTAL_PAGES), 0),
            currentOffset = prefs.getInt(getKey(bookId, KEY_OFFSET), 0),
            lastReadTimestamp = prefs.getLong(
                getKey(bookId, KEY_TIMESTAMP),
                System.currentTimeMillis()
            )
        )
    }

    fun saveProgress(progress: ReadingProgress) {
        prefs.edit().apply {
            putInt(getKey(progress.bookId, KEY_POSITION), progress.currentPosition)
            putInt(getKey(progress.bookId, KEY_TOTAL_SIZE), progress.totalSize)
            putInt(getKey(progress.bookId, KEY_CURRENT_PAGE), progress.currentPage)
            putInt(getKey(progress.bookId, KEY_TOTAL_PAGES), progress.totalPages)
            putInt(getKey(progress.bookId, KEY_OFFSET), progress.currentOffset)
            putLong(getKey(progress.bookId, KEY_TIMESTAMP), progress.lastReadTimestamp)
            apply()
        }
    }

    fun deleteProgress(bookId: String) {
        prefs.edit().apply {
            remove(getKey(bookId, KEY_POSITION))
            remove(getKey(bookId, KEY_TOTAL_SIZE))
            remove(getKey(bookId, KEY_CURRENT_PAGE))
            remove(getKey(bookId, KEY_TOTAL_PAGES))
            remove(getKey(bookId, KEY_OFFSET))
            remove(getKey(bookId, KEY_TIMESTAMP))
            apply()
        }
    }

    private fun getKey(bookId: String, suffix: String): String {
        return "${PREFIX}_${bookId}_${suffix}"
    }

    companion object {
        private const val PREFS_NAME = "reading_progress"
        private const val PREFIX = "progress"
        private const val KEY_POSITION = "position"
        private const val KEY_TOTAL_SIZE = "total_size"
        private const val KEY_CURRENT_PAGE = "current_page"
        private const val KEY_TOTAL_PAGES = "total_pages"
        private const val KEY_OFFSET = "offset"
        private const val KEY_TIMESTAMP = "timestamp"
    }
}

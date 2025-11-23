package max.keils.domain.repository

import max.keils.domain.entity.BookContent
import max.keils.domain.entity.ReadingProgress
import max.keils.domain.entity.ReaderSettings

interface ReaderRepository {

    suspend fun loadBookContent(localPath: String): BookContent

    suspend fun getReadingProgress(bookId: String): ReadingProgress?

    suspend fun saveReadingProgress(progress: ReadingProgress)

    suspend fun getReaderSettings(): ReaderSettings

    suspend fun saveReaderSettings(settings: ReaderSettings)
}


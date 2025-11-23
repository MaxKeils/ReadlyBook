package max.keils.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import max.keils.data.source.local.BookContentReader
import max.keils.data.source.local.ReadingProgressDataSource
import max.keils.data.source.local.ReaderSettingsDataSource
import max.keils.domain.entity.BookContent
import max.keils.domain.entity.ReadingProgress
import max.keils.domain.entity.ReaderSettings
import max.keils.domain.repository.ReaderRepository
import javax.inject.Inject

class ReaderRepositoryImpl @Inject constructor(
    private val bookContentReader: BookContentReader,
    private val readingProgressDataSource: ReadingProgressDataSource,
    private val readerSettingsDataSource: ReaderSettingsDataSource
) : ReaderRepository {

    override suspend fun loadBookContent(localPath: String): BookContent = withContext(Dispatchers.IO) {
        bookContentReader.readBook(localPath)
    }

    override suspend fun getReadingProgress(bookId: String): ReadingProgress? = withContext(Dispatchers.IO) {
        readingProgressDataSource.getProgress(bookId)
    }

    override suspend fun saveReadingProgress(progress: ReadingProgress) = withContext(Dispatchers.IO) {
        readingProgressDataSource.saveProgress(progress)
    }

    override suspend fun getReaderSettings(): ReaderSettings = withContext(Dispatchers.IO) {
        readerSettingsDataSource.getSettings()
    }

    override suspend fun saveReaderSettings(settings: ReaderSettings) = withContext(Dispatchers.IO) {
        readerSettingsDataSource.saveSettings(settings)
    }
}


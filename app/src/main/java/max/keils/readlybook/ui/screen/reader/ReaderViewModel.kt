package max.keils.readlybook.ui.screen.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import max.keils.domain.entity.BookContent
import max.keils.domain.entity.FontSize
import max.keils.domain.entity.LineSpacing
import max.keils.domain.entity.ReaderSettings
import max.keils.domain.entity.ReadingProgress
import max.keils.domain.entity.ReaderTheme
import max.keils.domain.usecase.DeleteBookEverywhereUseCase
import max.keils.domain.usecase.DeleteBookLocallyUseCase
import max.keils.domain.usecase.GetReadingProgressUseCase
import max.keils.domain.usecase.GetReaderSettingsUseCase
import max.keils.domain.usecase.GetUserBooksFromCacheUseCase
import max.keils.domain.usecase.LoadBookContentUseCase
import max.keils.domain.usecase.SaveReadingProgressUseCase
import max.keils.domain.usecase.SaveReaderSettingsUseCase
import javax.inject.Inject

class ReaderViewModel @Inject constructor(
    private val loadBookContentUseCase: LoadBookContentUseCase,
    private val getReadingProgressUseCase: GetReadingProgressUseCase,
    private val saveReadingProgressUseCase: SaveReadingProgressUseCase,
    private val getReaderSettingsUseCase: GetReaderSettingsUseCase,
    private val saveReaderSettingsUseCase: SaveReaderSettingsUseCase,
    private val getUserBooksFromCacheUseCase: GetUserBooksFromCacheUseCase,
    private val deleteBookLocallyUseCase: DeleteBookLocallyUseCase,
    private val deleteBookEverywhereUseCase: DeleteBookEverywhereUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ReaderState>(ReaderState.Loading)
    val state = _state.asStateFlow()

    private var saveProgressJob: Job? = null

    fun loadBook(bookId: String, userId: String) {
        viewModelScope.launch {
            try {
                _state.value = ReaderState.Loading

                val books = getUserBooksFromCacheUseCase(userId)
                val book = books?.find { it.id == bookId }

                if (book == null || book.localPath.isNullOrEmpty()) {
                    _state.value = ReaderState.Error(
                        message = "The book was not found or downloaded.",
                        bookId = bookId
                    )
                    return@launch
                }

                val localPath = book.localPath ?: return@launch
                val bookContent = loadBookContentUseCase(localPath)

                if (bookContent is BookContent.Error) {
                    _state.value = ReaderState.Error(
                        message = bookContent.message,
                        bookId = bookId
                    )
                    return@launch
                }

                val settings = getReaderSettingsUseCase()
                val savedProgress = getReadingProgressUseCase(bookId)

                val totalSize = when (bookContent) {
                    is BookContent.Text -> {
                        val length = bookContent.content.length
                        val chunkSize = 1000
                        if (length == 0) 0 else ((length + chunkSize - 1) / chunkSize)
                    }
                    is BookContent.Epub -> bookContent.chapters.size * 2
                    is BookContent.Pdf -> bookContent.pageCount
                    else -> 0
                }

                val maxPosition = if (totalSize > 0) totalSize - 1 else 0
                val progress = if (savedProgress != null) {
                    savedProgress.copy(
                        totalSize = totalSize,
                        currentPosition = savedProgress.currentPosition.coerceIn(0, maxPosition)
                    )
                } else {
                    ReadingProgress(
                        bookId = bookId,
                        totalSize = totalSize,
                        totalPages = if (bookContent is BookContent.Pdf) bookContent.pageCount else 0
                    )
                }

                _state.value = ReaderState.Success(
                    bookContent = bookContent,
                    settings = settings,
                    progress = progress
                )
            } catch (e: Exception) {
                _state.value = ReaderState.Error(
                    message = "Error loading the book: ${e.message}",
                    bookId = bookId
                )
            }
        }
    }

    fun updateReadingPosition(position: Int, offset: Int = 0) {
        val currentState = _state.value
        if (currentState is ReaderState.Success) {
            val newProgress = currentState.progress.copy(
                currentPosition = position,
                currentOffset = offset.coerceAtLeast(0),
                lastReadTimestamp = System.currentTimeMillis()
            )

            _state.value = currentState.copy(progress = newProgress)

            saveProgressJob?.cancel()
            saveProgressJob = viewModelScope.launch {
                delay(2000)
                saveReadingProgressUseCase(newProgress)
            }
        }
    }

    fun updateCurrentPage(page: Int) {
        val currentState = _state.value
        if (currentState is ReaderState.Success) {
            val newProgress = currentState.progress.copy(
                currentPage = page,
                lastReadTimestamp = System.currentTimeMillis()
            )
            _state.value = currentState.copy(progress = newProgress)

            saveProgressJob?.cancel()
            saveProgressJob = viewModelScope.launch {
                delay(2000)
                saveReadingProgressUseCase(newProgress)
            }
        }
    }

    fun toggleSettingsVisibility() {
        val currentState = _state.value
        if (currentState is ReaderState.Success) {
            _state.value = currentState.copy(
                isSettingsVisible = !currentState.isSettingsVisible
            )
        }
    }

    fun updateFontSize(fontSize: FontSize) {
        updateSettings { it.copy(fontSize = fontSize) }
    }

    fun updateLineSpacing(lineSpacing: LineSpacing) {
        updateSettings { it.copy(lineSpacing = lineSpacing) }
    }

    fun updateTheme(theme: ReaderTheme) {
        updateSettings { it.copy(theme = theme) }
    }

    private fun updateSettings(transform: (ReaderSettings) -> ReaderSettings) {
        val currentState = _state.value
        if (currentState is ReaderState.Success) {
            val newSettings = transform(currentState.settings)
            _state.value = currentState.copy(settings = newSettings)
            viewModelScope.launch {
                saveReaderSettingsUseCase(newSettings)
            }
        }
    }

    fun deleteBook(bookId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                deleteBookLocallyUseCase(bookId)
                onDeleted()
            } catch (_: Exception) {
            }
        }
    }

    fun deleteBookEverywhere(bookId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                deleteBookEverywhereUseCase(bookId)
                onDeleted()
            } catch (_: Exception) {
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveProgressJob?.cancel()
        val currentState = _state.value
        if (currentState is ReaderState.Success) {
            runBlocking {
                saveReadingProgressUseCase(currentState.progress)
            }
        }
    }
}

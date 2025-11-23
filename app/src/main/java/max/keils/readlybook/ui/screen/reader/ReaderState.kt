package max.keils.readlybook.ui.screen.reader

import max.keils.domain.entity.BookContent
import max.keils.domain.entity.ReadingProgress
import max.keils.domain.entity.ReaderSettings

sealed class ReaderState {
    data object Loading : ReaderState()

    data class Success(
        val bookContent: BookContent,
        val settings: ReaderSettings,
        val progress: ReadingProgress,
        val isSettingsVisible: Boolean = false
    ) : ReaderState()

    data class Error(
        val message: String,
        val bookId: String
    ) : ReaderState()
}


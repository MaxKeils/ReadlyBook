package max.keils.domain.usecase

import max.keils.domain.entity.BookContent
import max.keils.domain.repository.ReaderRepository

class LoadBookContentUseCase(
    private val readerRepository: ReaderRepository
) {
    suspend operator fun invoke(localPath: String): BookContent {
        return readerRepository.loadBookContent(localPath)
    }
}
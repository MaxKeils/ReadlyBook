package max.keils.domain.usecase

import max.keils.domain.entity.ReadingProgress
import max.keils.domain.repository.ReaderRepository

class GetReadingProgressUseCase(
    private val readerRepository: ReaderRepository
) {
    suspend operator fun invoke(bookId: String): ReadingProgress? {
        return readerRepository.getReadingProgress(bookId)
    }
}


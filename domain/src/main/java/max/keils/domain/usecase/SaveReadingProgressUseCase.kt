package max.keils.domain.usecase

import max.keils.domain.entity.ReadingProgress
import max.keils.domain.repository.ReaderRepository

class SaveReadingProgressUseCase(
    private val readerRepository: ReaderRepository
) {
    suspend operator fun invoke(progress: ReadingProgress) {
        readerRepository.saveReadingProgress(progress)
    }
}
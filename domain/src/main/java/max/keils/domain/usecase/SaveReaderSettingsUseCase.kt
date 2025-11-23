package max.keils.domain.usecase

import max.keils.domain.entity.ReaderSettings
import max.keils.domain.repository.ReaderRepository

class SaveReaderSettingsUseCase(
    private val readerRepository: ReaderRepository
) {
    suspend operator fun invoke(settings: ReaderSettings) {
        readerRepository.saveReaderSettings(settings)
    }
}


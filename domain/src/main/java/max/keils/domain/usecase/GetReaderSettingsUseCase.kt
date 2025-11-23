package max.keils.domain.usecase

import max.keils.domain.entity.ReaderSettings
import max.keils.domain.repository.ReaderRepository

class GetReaderSettingsUseCase(
    private val readerRepository: ReaderRepository
) {
    suspend operator fun invoke(): ReaderSettings {
        return readerRepository.getReaderSettings()
    }
}


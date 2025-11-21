package max.keils.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import max.keils.domain.repository.AuthRepository

class GetCurrentUserIdUseCase(private val repository: AuthRepository) {

    operator fun invoke(): StateFlow<String?> =
        repository.currentUserId

}
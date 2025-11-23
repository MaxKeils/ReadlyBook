package max.keils.domain.usecase

import max.keils.domain.entity.UserData
import max.keils.domain.repository.AuthRepository

class GetCurrentUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(): Result<UserData?> =
        repository.getCurrentUser()

}
package max.keils.domain.usecase

import max.keils.domain.entity.UserData
import max.keils.domain.repository.AuthRepository

class UpdateUserProfileUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(displayName: String?, photoUrl: String?): Result<UserData> =
        repository.updateUserProfile(displayName, photoUrl)

}


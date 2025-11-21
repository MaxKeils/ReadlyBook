package max.keils.domain.usecase

import max.keils.domain.entity.UserData
import max.keils.domain.repository.AuthRepository

class SignInUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<UserData> =
        repository.signIn(email, password)

}
package max.keils.domain.usecase

import max.keils.domain.repository.AuthRepository

class SignUpUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String) =
        repository.signUp(email, password)

}
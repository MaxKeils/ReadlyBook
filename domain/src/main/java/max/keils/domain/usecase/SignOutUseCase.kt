package max.keils.domain.usecase

import max.keils.domain.repository.AuthRepository

class SignOutUseCase(private val repository: AuthRepository) {

    operator fun invoke() =
        repository.signOut()

}
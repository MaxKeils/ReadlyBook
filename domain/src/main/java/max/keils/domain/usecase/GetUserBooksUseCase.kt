package max.keils.domain.usecase

import max.keils.domain.repository.BookRepository

class GetUserBooksUseCase(
    private val repository: BookRepository,
) {

    operator fun invoke(userId: String) = repository.getUserBooks(userId)

}
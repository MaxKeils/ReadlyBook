package max.keils.domain.usecase

import max.keils.domain.repository.BookRepository

class DeleteBookEverywhereUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: String): Boolean {
        return repository.deleteBookEverywhere(bookId)
    }
}
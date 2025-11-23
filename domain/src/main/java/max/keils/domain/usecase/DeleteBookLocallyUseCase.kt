package max.keils.domain.usecase

import max.keils.domain.repository.BookRepository

class DeleteBookLocallyUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: String): Boolean {
        return repository.deleteBookLocally(bookId)
    }
}
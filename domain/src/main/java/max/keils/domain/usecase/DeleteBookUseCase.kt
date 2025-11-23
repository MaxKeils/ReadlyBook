package max.keils.domain.usecase

import max.keils.domain.repository.BookRepository

class DeleteBookUseCase(
    private val repository: BookRepository
) {

    suspend operator fun invoke(bookId: String): Boolean = repository.deleteBook(bookId)
}
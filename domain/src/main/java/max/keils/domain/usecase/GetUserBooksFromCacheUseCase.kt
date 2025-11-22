package max.keils.domain.usecase

import max.keils.domain.entity.Book
import max.keils.domain.repository.BookRepository

class GetUserBooksFromCacheUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(userId: String): List<Book>? {
        return repository.getUserBooksFromCache(userId)
    }
}
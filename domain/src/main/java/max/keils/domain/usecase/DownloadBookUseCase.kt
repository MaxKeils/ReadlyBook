package max.keils.domain.usecase

import max.keils.domain.entity.Book
import max.keils.domain.repository.BookRepository

class DownloadBookUseCase(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: String): Book {
        return bookRepository.downloadBook(bookId)
    }
}

package max.keils.domain.usecase

import max.keils.domain.entity.Book
import max.keils.domain.repository.BookRepository

class UploadBookUseCase(private val repository: BookRepository) {

    suspend operator fun invoke(
        book: Book,
        fileBytes: ByteArray,
        onProgress: suspend (Float) -> Unit
    ): String {
        return repository.uploadBook(book, fileBytes, onProgress)
    }
}
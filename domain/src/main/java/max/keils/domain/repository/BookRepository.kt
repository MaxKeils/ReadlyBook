package max.keils.domain.repository

import max.keils.domain.entity.Book

interface BookRepository {

    suspend fun uploadBook(
        book: Book,
        fileBytes: ByteArray,
        onProgress: suspend (Float) -> Unit
    ): String

    suspend fun getUserBooks(userId: String): List<Book>

    suspend fun getBookById(bookId: String): Book?

    suspend fun deleteBook(bookId: String): Boolean

}
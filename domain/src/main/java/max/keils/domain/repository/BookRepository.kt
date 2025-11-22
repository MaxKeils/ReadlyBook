package max.keils.domain.repository

import kotlinx.coroutines.flow.Flow
import max.keils.domain.entity.Book

interface BookRepository {

    suspend fun uploadBook(
        book: Book,
        fileBytes: ByteArray,
        onProgress: suspend (Float) -> Unit
    ): String

    fun getUserBooks(userId: String): Flow<List<Book>>

    suspend fun getBookById(bookId: String): Book?

    suspend fun deleteBook(bookId: String): Boolean

}
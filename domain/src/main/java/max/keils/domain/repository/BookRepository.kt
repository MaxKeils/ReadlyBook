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

    suspend fun downloadBook(bookId: String): Book

    suspend fun getUserBooksFromCache(userId: String): List<Book>?

    suspend fun syncUserBooksWithFirebase(userId: String): List<Book>

    suspend fun deleteBookLocally(bookId: String): Boolean

    suspend fun deleteBookEverywhere(bookId: String): Boolean

}
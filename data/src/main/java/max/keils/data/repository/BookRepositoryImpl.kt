package max.keils.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import max.keils.data.source.local.BookCacheManager
import max.keils.data.source.remote.FirebaseBookRemoteDataSource
import max.keils.domain.entity.Book
import max.keils.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirebaseBookRemoteDataSource,
    private val cacheManager: BookCacheManager
) : BookRepository {

    override suspend fun uploadBook(
        book: Book,
        fileBytes: ByteArray,
        onProgress: suspend (Float) -> Unit
    ): String {
        return remoteDataSource.uploadBook(
            book = book,
            fileBytes = fileBytes,
            onProgress = onProgress
        )
    }

    override fun getUserBooks(userId: String): Flow<List<Book>> {
        return remoteDataSource.getUserBooks(userId).map { remoteBooks ->
            val cachedIds = cacheManager.getCachedBooks()
            remoteBooks.map { book ->
                val localFile =
                    if (cachedIds.contains(book.id)) cacheManager.getBookFromCache(bookId = book.id)
                    else null
                book.copy(localPath = localFile?.absolutePath)
            }
        }
    }

    override suspend fun getBookById(bookId: String): Book? {
        TODO()
    }

    override suspend fun deleteBook(bookId: String): Boolean {
        val removedFromCache = cacheManager.removeBookFromCache(bookId)
        val removedFromFirestore = remoteDataSource.deleteBook(bookId)
        return removedFromCache || removedFromFirestore
    }
}

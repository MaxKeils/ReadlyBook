package max.keils.data.repository

import max.keils.data.source.remote.FirebaseBookRemoteDataSource
import max.keils.domain.entity.Book
import max.keils.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirebaseBookRemoteDataSource,
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

    override suspend fun getUserBooks(userId: String): List<Book> {
        TODO()
    }

    override suspend fun getBookById(bookId: String): Book? {
        TODO()
    }

    override suspend fun deleteBook(bookId: String): Boolean {
        TODO()
    }

}

package max.keils.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import max.keils.data.mapper.BookMapper
import max.keils.data.source.local.BookCacheManager
import max.keils.data.source.local.dao.BookDao
import max.keils.data.source.remote.FirebaseBookRemoteDataSource
import max.keils.domain.entity.Book
import max.keils.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirebaseBookRemoteDataSource,
    private val cacheManager: BookCacheManager,
    private val bookDao: BookDao,
    private val bookMapper: BookMapper
) : BookRepository {

    override suspend fun uploadBook(
        book: Book,
        fileBytes: ByteArray,
        onProgress: suspend (Float) -> Unit
    ): String {

        val bookId = remoteDataSource.uploadBook(
            book = book,
            fileBytes = fileBytes,
            onProgress = onProgress
        )

        val localPath = cacheManager.saveBookToCache(
            bookId = bookId,
            fileBytes = fileBytes,
            fileName = book.fileName
        )
        val uploadedBook = book.copy(id = bookId, localPath = localPath)
        val entity = bookMapper.mapDomainToEntity(uploadedBook)
        bookDao.insertBook(entity)

        return bookId
    }

    override fun getUserBooks(userId: String): Flow<List<Book>> {
        return bookDao.getUserBooksFlow(userId).map { entities ->
            entities.map { bookMapper.mapEntityToDomain(it) }
        }
    }

    override suspend fun getBookById(bookId: String): Book? {
        return remoteDataSource.getBookById(bookId)
    }

    override suspend fun deleteBook(bookId: String): Boolean {
        val removedFromCache = cacheManager.removeBookFromCache(bookId)
        val removedFromFirestore = remoteDataSource.deleteBook(bookId)
        return removedFromCache || removedFromFirestore
    }

    override suspend fun downloadBook(bookId: String): Book {
        val book = remoteDataSource.getBookById(bookId)
            ?: throw IllegalArgumentException("Book not found: $bookId")

        if (cacheManager.isBookCached(bookId)) {
            val cachedFile = cacheManager.getBookFromCache(bookId)
            if (cachedFile != null) {
                return book.copy(localPath = cachedFile.absolutePath)
            }
        }

        val fileBytes = remoteDataSource.downloadBookFromStorage(fileUrl = book.fileUrl)

        val localPath = cacheManager.saveBookToCache(
            bookId = bookId,
            fileBytes = fileBytes,
            fileName = book.fileName
        )

        val updatedBook = book.copy(localPath = localPath)
        val bookEntity = bookMapper.mapDomainToEntity(updatedBook)
        bookDao.insertBook(bookEntity)
        return updatedBook
    }

    override suspend fun getUserBooksFromCache(userId: String): List<Book>? {
        val entities = bookDao.getUserBooks(userId)
        if (entities.isEmpty()) {
            return null
        }

        val books = entities.map { bookMapper.mapEntityToDomain(it) }
        return books
    }

    override suspend fun syncUserBooksWithFirebase(userId: String): List<Book> {
        val remoteBooks = remoteDataSource.getUserBooks(userId).first()
        val cachedIds = cacheManager.getCachedBooks()

        val booksWithLocalPath = remoteBooks.map { book ->
            val isCached = cachedIds.contains(book.id)
            val localFile = if (isCached) {
                cacheManager.getBookFromCache(bookId = book.id)
            } else {
                null
            }
            book.copy(localPath = localFile?.absolutePath)
        }

        val entities = booksWithLocalPath.map { bookMapper.mapDomainToEntity(it) }
        bookDao.insertBooks(entities)
        return booksWithLocalPath
    }

    override suspend fun deleteBookLocally(bookId: String): Boolean {
        val removedFromCache = cacheManager.removeBookFromCache(bookId)
        bookDao.updateLocalPath(bookId, null)
        return removedFromCache
    }

    override suspend fun deleteBookEverywhere(bookId: String): Boolean {
        val removedFromCache = cacheManager.removeBookFromCache(bookId)
        val removedFromFirestore = remoteDataSource.deleteBook(bookId)
        bookDao.deleteBookById(bookId)

        return removedFromCache || removedFromFirestore
    }
}


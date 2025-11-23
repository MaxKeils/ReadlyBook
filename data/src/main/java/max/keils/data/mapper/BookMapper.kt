package max.keils.data.mapper

import max.keils.data.source.local.entity.BookEntity
import max.keils.domain.entity.Book
import javax.inject.Inject

class BookMapper @Inject constructor() {

    fun mapDomainToFirestoreMap(book: Book): Map<String, Any> = mutableMapOf(
        "title" to book.title,
        "author" to book.author,
        "fileUrl" to book.fileUrl,
        "userId" to book.userId,
        "fileName" to book.fileName,
        "uploadedAt" to book.uploadedAt
    ).apply {
        book.coverUrl?.let {
            this["coverUrl"] = it
        }
    }

    fun mapFirestoreMapToDomain(id: String, map: Map<String, Any>): Book = Book(
        id = id,
        title = map["title"] as? String ?: "",
        author = map["author"] as? String ?: "",
        fileUrl = map["fileUrl"] as? String ?: "",
        userId = map["userId"] as? String ?: "",
        fileName = map["fileName"] as? String ?: "",
        coverUrl = map["coverUrl"] as? String,
        uploadedAt = map["uploadedAt"] as? Long ?: System.currentTimeMillis(),
        localPath = null
    )

    fun mapDomainToEntity(book: Book): BookEntity {
        return BookEntity(
            id = book.id,
            title = book.title,
            author = book.author,
            fileUrl = book.fileUrl,
            userId = book.userId,
            fileName = book.fileName,
            localPath = book.localPath,
            coverUrl = book.coverUrl,
            uploadedAt = book.uploadedAt,
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    fun mapEntityToDomain(entity: BookEntity): Book = Book(
        id = entity.id,
        title = entity.title,
        author = entity.author,
        fileUrl = entity.fileUrl,
        userId = entity.userId,
        fileName = entity.fileName,
        localPath = entity.localPath,
        coverUrl = entity.coverUrl,
        uploadedAt = entity.uploadedAt
    )
}


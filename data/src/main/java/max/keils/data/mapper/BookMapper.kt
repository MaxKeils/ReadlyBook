package max.keils.data.mapper

import max.keils.domain.entity.Book
import javax.inject.Inject

class BookMapper @Inject constructor() {

    fun mapEntityToFirestoreMap(book: Book): Map<String, Any> = mapOf(
        "title" to book.title,
        "author" to book.author,
        "fileUrl" to book.fileUrl,
        "userId" to book.userId,
        "fileName" to book.fileName,
        "uploadedAt" to book.uploadedAt
    )

    fun mapFirestoreMapToEntity(id: String, map: Map<String, Any>): Book {
        return Book(
            id = id,
            title = map["title"] as? String ?: "",
            author = map["author"] as? String ?: "",
            fileUrl = map["fileUrl"] as? String ?: "",
            userId = map["userId"] as? String ?: "",
            fileName = map["fileName"] as? String ?: "",
            uploadedAt = map["uploadedAt"] as? Long ?: System.currentTimeMillis(),
            localPath = null
        )
    }
}


package max.keils.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import max.keils.data.mapper.BookMapper
import max.keils.domain.entity.Book
import javax.inject.Inject

class FirebaseBookRemoteDataSource @Inject constructor(
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
    private val bookMapper: BookMapper
) {

    suspend fun uploadBook(
        book: Book,
        fileBytes: ByteArray,
        onProgress: suspend (Float) -> Unit
    ): String {
        val allowedExtensions = listOf("pdf", "epub", "txt")
        val fileExtension = book.fileName.substringAfterLast('.', "").lowercase()

        if (fileExtension !in allowedExtensions) {
            throw IllegalArgumentException("Unsupported file format: .$fileExtension")
        }

        val storageRef = storage.reference.child("books/${book.userId}/${book.fileName}")
        val uploadTask = storageRef.putBytes(fileBytes)

        val scope = CoroutineScope(Dispatchers.IO)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount
            scope.launch {
                onProgress(progress)
            }
        }.await()

        val downloadUri = storageRef.downloadUrl.await()

        val bookWithUrl = book.copy(fileUrl = downloadUri.toString())

        val bookMap = bookMapper.mapEntityToFirestoreMap(bookWithUrl)
        val docRef = firestore.collection("books").add(bookMap).await()

        return docRef.id
    }

        fun getUserBooks(userId: String): Flow<List<Book>> = callbackFlow {
        val listener = firestore.collection("books")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val books = snapshot.documents.mapNotNull { document ->
                    val map = document.data ?: return@mapNotNull null
                    val book = bookMapper.mapFirestoreMapToEntity(document.id, map)
                    book
                }
                trySend(books)
            }
        awaitClose { listener.remove() }
    }

    companion object {
        private const val TAG = "FirebaseBookRemoteDataSource"
    }
}
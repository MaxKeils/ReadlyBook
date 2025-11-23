package max.keils.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import max.keils.data.source.local.entity.BookEntity

@Dao
interface BookDao {

    @Query("SELECT * FROM books WHERE userId = :userId ORDER BY uploadedAt DESC")
    fun getUserBooksFlow(userId: String): Flow<List<BookEntity>>


    @Query("SELECT * FROM books WHERE userId = :userId ORDER BY uploadedAt DESC")
    suspend fun getUserBooks(userId: String): List<BookEntity>


    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    suspend fun getBookById(bookId: String): BookEntity?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)


    @Update
    suspend fun updateBook(book: BookEntity)


    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: String)


    @Query("UPDATE books SET localPath = :localPath WHERE id = :bookId")
    suspend fun updateLocalPath(bookId: String, localPath: String?)


    @Query("DELETE FROM books WHERE userId = :userId")
    suspend fun deleteAllUserBooks(userId: String)


    @Query("SELECT COUNT(*) FROM books WHERE userId = :userId")
    suspend fun getUserBooksCount(userId: String): Int
}


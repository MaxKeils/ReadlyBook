package max.keils.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val fileUrl: String,
    val userId: String,
    val fileName: String,
    val localPath: String?,
    val coverUrl: String?,
    val uploadedAt: Long,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
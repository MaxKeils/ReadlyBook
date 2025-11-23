package max.keils.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import max.keils.data.source.local.dao.BookDao
import max.keils.data.source.local.entity.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}


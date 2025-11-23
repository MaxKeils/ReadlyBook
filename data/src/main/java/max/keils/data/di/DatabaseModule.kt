package max.keils.data.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import max.keils.data.source.local.dao.BookDao
import max.keils.data.source.local.database.AppDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "readly_book_database"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideBookDao(database: AppDatabase): BookDao {
        return database.bookDao()
    }
}


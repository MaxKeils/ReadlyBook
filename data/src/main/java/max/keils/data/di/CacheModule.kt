package max.keils.data.di

import android.app.Application
import dagger.Module
import dagger.Provides
import max.keils.data.source.local.BookCacheManager
import javax.inject.Singleton

@Module
class CacheModule {

    @Provides
    @Singleton
    fun provideBookCacheManager(context: Application): BookCacheManager {
        return BookCacheManager(context)
    }
}


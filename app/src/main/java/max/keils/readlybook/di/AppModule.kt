package max.keils.readlybook.di

import dagger.Module
import max.keils.data.di.CacheModule
import max.keils.data.di.RepositoryModule
import max.keils.data.di.WorkerFactoryModule
import max.keils.data.di.WorkerModule

@Module(
    includes = [
        FirebaseModule::class,
        UseCaseModule::class,
        RepositoryModule::class,
        ViewModelModule::class,
        WorkerModule::class,
        WorkerFactoryModule::class,
        CacheModule::class
    ]
)
object AppModule
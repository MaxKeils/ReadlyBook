package max.keils.readlybook.di

import dagger.Module
import max.keils.data.di.RepositoryModule

@Module(
    includes = [
        FirebaseModule::class,
        UseCaseModule::class,
        RepositoryModule::class,
        ViewModelModule::class
    ]
)
object AppModule
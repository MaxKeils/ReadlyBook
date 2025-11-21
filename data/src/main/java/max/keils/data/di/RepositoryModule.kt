package max.keils.data.di

import dagger.Binds
import dagger.Module
import max.keils.data.repository.AuthRepositoryImpl
import max.keils.domain.repository.AuthRepository

@Module
interface RepositoryModule {

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

}
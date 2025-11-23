package max.keils.data.di

import dagger.Binds
import dagger.Module
import max.keils.data.repository.AuthRepositoryImpl
import max.keils.data.repository.BookRepositoryImpl
import max.keils.data.repository.ReaderRepositoryImpl
import max.keils.domain.repository.AuthRepository
import max.keils.domain.repository.BookRepository
import max.keils.domain.repository.ReaderRepository

@Module
interface RepositoryModule {

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindBookRepository(impl: BookRepositoryImpl): BookRepository

    @Binds
    fun bindReaderRepository(impl: ReaderRepositoryImpl): ReaderRepository

}
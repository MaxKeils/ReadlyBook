package max.keils.readlybook.di

import dagger.Module
import dagger.Provides
import max.keils.domain.repository.AuthRepository
import max.keils.domain.repository.BookRepository
import max.keils.domain.usecase.GetCurrentUserIdUseCase
import max.keils.domain.usecase.GetUserBooksUseCase
import max.keils.domain.usecase.SignInUseCase
import max.keils.domain.usecase.SignOutUseCase
import max.keils.domain.usecase.SignUpUseCase
import max.keils.domain.usecase.UploadBookUseCase

@Module
object UseCaseModule {

    @Provides
    fun provideSignInUseCase(repository: AuthRepository): SignInUseCase = SignInUseCase(repository)

    @Provides
    fun provideSignOutUseCase(repository: AuthRepository): SignOutUseCase =
        SignOutUseCase(repository)

    @Provides
    fun provideSignUpUseCase(repository: AuthRepository): SignUpUseCase = SignUpUseCase(repository)

    @Provides
    fun provideGetCurrentUserUseCase(repository: AuthRepository) =
        GetCurrentUserIdUseCase(repository)

    @Provides
    fun provideUploadBookUseCase(repository: BookRepository) =
        UploadBookUseCase(repository)

    @Provides
    fun provideGetUserBooksUseCase(repository: BookRepository) =
        GetUserBooksUseCase(repository)

    @Provides
    fun provideDeleteBookUseCase(repository: BookRepository) =
        max.keils.domain.usecase.DeleteBookUseCase(repository)

}
package max.keils.readlybook.di

import dagger.Module
import dagger.Provides
import max.keils.domain.repository.AuthRepository
import max.keils.domain.repository.BookRepository
import max.keils.domain.repository.ReaderRepository
import max.keils.domain.usecase.*

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
    fun provideGetCurrentUserIdUseCase(repository: AuthRepository) =
        GetCurrentUserIdUseCase(repository)

    @Provides
    fun provideGetCurrentUserUseCase(repository: AuthRepository) =
        GetCurrentUserUseCase(repository)

    @Provides
    fun provideUpdateUserProfileUseCase(repository: AuthRepository) =
        UpdateUserProfileUseCase(repository)

    @Provides
    fun provideUploadUserPhotoUseCase(repository: AuthRepository) =
        UploadUserPhotoUseCase(repository)

    @Provides
    fun provideUploadBookUseCase(repository: BookRepository) =
        UploadBookUseCase(repository)

    @Provides
    fun provideGetUserBooksUseCase(repository: BookRepository) =
        GetUserBooksUseCase(repository)

    @Provides
    fun provideDeleteBookUseCase(repository: BookRepository) =
        DeleteBookUseCase(repository)

    @Provides
    fun provideDownloadBookUseCase(repository: BookRepository) =
        DownloadBookUseCase(repository)

    @Provides
    fun provideGetUserBooksFromCacheUseCase(repository: BookRepository) =
        GetUserBooksFromCacheUseCase(repository)

    @Provides
    fun provideSyncUserBooksWithFirebaseUseCase(repository: BookRepository) =
        SyncUserBooksWithFirebaseUseCase(repository)

    @Provides
    fun provideDeleteBookLocallyUseCase(repository: BookRepository) =
        DeleteBookLocallyUseCase(repository)

    @Provides
    fun provideDeleteBookEverywhereUseCase(repository: BookRepository) =
        DeleteBookEverywhereUseCase(repository)

    @Provides
    fun provideLoadBookContentUseCase(repository: ReaderRepository) =
        LoadBookContentUseCase(repository)

    @Provides
    fun provideGetReadingProgressUseCase(repository: ReaderRepository) =
        GetReadingProgressUseCase(repository)

    @Provides
    fun provideSaveReadingProgressUseCase(repository: ReaderRepository) =
        SaveReadingProgressUseCase(repository)

    @Provides
    fun provideGetReaderSettingsUseCase(repository: ReaderRepository) =
        GetReaderSettingsUseCase(repository)

    @Provides
    fun provideSaveReaderSettingsUseCase(repository: ReaderRepository) =
        SaveReaderSettingsUseCase(repository)
}

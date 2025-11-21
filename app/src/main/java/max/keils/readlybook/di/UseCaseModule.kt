package max.keils.readlybook.di

import dagger.Module
import dagger.Provides
import max.keils.domain.repository.AuthRepository
import max.keils.domain.usecase.SignInUseCase

@Module
object UseCaseModule {

    @Provides
    fun provideSignInUseCase(repository: AuthRepository): SignInUseCase = SignInUseCase(repository)

}
package max.keils.readlybook.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import max.keils.readlybook.ui.screen.auth.AuthViewModel
import max.keils.readlybook.ui.screen.upload.UploadBookViewModel

@Module
interface ViewModelModule {

    @Binds
    @ViewModelKey(AuthViewModel::class)
    @IntoMap
    fun bindAuthViewModel(viewModel: AuthViewModel): ViewModel

    @Binds
    @ViewModelKey(UploadBookViewModel::class)
    @IntoMap
    fun bindUploadBookViewModel(viewModel: UploadBookViewModel): ViewModel

}
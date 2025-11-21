package max.keils.readlybook.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module

@Module
class FirebaseModule {

    @ApplicationScope
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

}
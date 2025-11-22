package max.keils.readlybook.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import max.keils.readlybook.App
import max.keils.readlybook.ui.MainActivity
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class]
)
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(application: App)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(context: Application): Builder

        fun build(): AppComponent
    }

}
package max.keils.readlybook

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkerFactory
import max.keils.readlybook.di.AppComponent
import max.keils.readlybook.di.DaggerAppComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: WorkerFactory

    val component by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> component
        else -> this.applicationContext.appComponent
    }
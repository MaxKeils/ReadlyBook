package max.keils.readlybook

import android.app.Application
import android.content.Context
import max.keils.readlybook.di.AppComponent
import max.keils.readlybook.di.ApplicationScope
import max.keils.readlybook.di.DaggerAppComponent

@ApplicationScope
class App : Application() {

    val component by lazy { DaggerAppComponent.create() }


}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> component
        else -> this.applicationContext.appComponent
    }
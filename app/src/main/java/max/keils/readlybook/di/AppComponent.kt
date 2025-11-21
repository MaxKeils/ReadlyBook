package max.keils.readlybook.di

import dagger.Component
import max.keils.readlybook.ui.MainActivity

@ApplicationScope
@Component(
    modules = [AppModule::class]
)
interface AppComponent
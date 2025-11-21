package max.keils.readlybook.di

import dagger.Component

@ApplicationScope
@Component(
    modules = [AppModule::class]
)
interface AppComponent
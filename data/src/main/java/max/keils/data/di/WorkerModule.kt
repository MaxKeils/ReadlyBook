package max.keils.data.di

import dagger.Binds
import dagger.Module
import max.keils.data.worker.UploadBookWorkerFactory

@Module
interface WorkerModule {

    @Binds
    fun bindUploadBookWorkerFactory(impl: UploadBookWorkerAssistedFactoryImpl): UploadBookWorkerFactory

}
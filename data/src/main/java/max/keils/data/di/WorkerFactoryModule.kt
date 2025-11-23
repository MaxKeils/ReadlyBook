package max.keils.data.di

import androidx.work.WorkerFactory
import dagger.Module
import dagger.Provides
import max.keils.data.worker.DaggerWorkerFactory
import max.keils.data.worker.UploadBookWorker
import max.keils.data.worker.UploadBookWorkerFactory
import javax.inject.Singleton

@Module
class WorkerFactoryModule {

    @Provides
    @Singleton
    fun provideWorkerFactory(uploadBookWorkerFactory: UploadBookWorkerFactory): WorkerFactory =
        DaggerWorkerFactory(
            mapOf(
                UploadBookWorker::class.java to uploadBookWorkerFactory
            )
        )

}
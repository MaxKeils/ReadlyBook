package max.keils.data.di

import android.content.Context
import androidx.work.WorkerParameters
import max.keils.data.source.local.BookCacheManager
import max.keils.data.worker.UploadBookWorker
import max.keils.data.worker.UploadBookWorkerFactory
import max.keils.domain.repository.BookRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadBookWorkerAssistedFactoryImpl @Inject constructor(
    private val bookRepository: BookRepository,
    private val cacheManager: BookCacheManager
) : UploadBookWorkerFactory {

    override fun create(
        appContext: Context,
        params: WorkerParameters
    ): UploadBookWorker =
        UploadBookWorker(
            appContext = appContext,
            workerParams = params,
            repository = bookRepository,
            cacheManager = cacheManager
        )
}
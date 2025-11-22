package max.keils.data.worker

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class DaggerWorkerFactory(
    private val factories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards WorkerAssistedFactory<out ListenableWorker>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        val clazz = Class.forName(workerClassName).asSubclass(ListenableWorker::class.java)
        val factory = factories[clazz]
            ?: throw IllegalArgumentException("Unknown worker class: $workerClassName")

        Log.d("DaggerWorkerFactory", "Creating worker with factory: $factory")
        return factory.create(appContext, workerParameters)
    }
}
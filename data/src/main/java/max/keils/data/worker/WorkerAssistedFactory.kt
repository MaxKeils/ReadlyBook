package max.keils.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface WorkerAssistedFactory<T : ListenableWorker> {
    fun create(appContext: Context, params: WorkerParameters): T
}
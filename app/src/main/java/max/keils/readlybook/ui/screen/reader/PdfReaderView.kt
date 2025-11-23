package max.keils.readlybook.ui.screen.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import com.rajat.pdfviewer.util.PdfSource
import java.io.File

@Composable
internal fun PdfReaderView(
    filePath: String,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    onPageChanged: (Int) -> Unit
) {
    val file = File(filePath)

    if (!file.exists()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PDF not found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        PdfRendererViewCompose(
            source = PdfSource.LocalFile(file),
            lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current,
            statusCallBack = object : PdfRendererView.StatusCallBack {
                override fun onPageChanged(currentPage: Int, totalPage: Int) {
                    onPageChanged(currentPage)
                }

                override fun onPdfLoadStart() {}

                override fun onPdfLoadProgress(
                    progress: Int,
                    downloadedBytes: Long,
                    totalBytes: Long?
                ) {
                }

                override fun onPdfLoadSuccess(absolutePath: String) {}

                override fun onError(error: Throwable) {
                    error.printStackTrace()
                }

            }
        )
    }
}


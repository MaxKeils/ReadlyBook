package max.keils.readlybook.ui.screen.upload

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import max.keils.readlybook.R
import max.keils.readlybook.ui.components.LottieAnimation
import max.keils.readlybook.ui.components.ReadlyTextField
import max.keils.readlybook.ui.theme.ReadlyBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadBookScreen(viewModel: UploadBookViewModel = viewModel()) {
    val context = LocalContext.current

    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    val (title, setTitle) = remember { mutableStateOf("") }
    val (author, setAuthor) = remember { mutableStateOf("") }
    val (coverUrl, setCoverUrl) = remember { mutableStateOf("") }

    val state by viewModel.state.collectAsState()
    var isInputMetadataBottomSheetExpanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fileUri = it
            fileName = viewModel.getFileName(it)
            isInputMetadataBottomSheetExpanded = true
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.upload_book)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val state = state) {
                UploadBookState.Idle -> {
                    LottieAnimation(rawRes = R.raw.waiting_start_upload)

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { launcher.launch("*/*") },
                        enabled = !isInputMetadataBottomSheetExpanded
                    ) {
                        Text(text = stringResource(R.string.select_file))
                    }
                }

                is UploadBookState.Uploading -> {
                    val progress = state.progress

                    val text = stringResource(R.string.loading) + "${(progress * 100).toInt()}%"
                    Text(text)

                    Spacer(Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        viewModel.cancelUpload()
                    }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }

                UploadBookState.Success -> {
                    LottieAnimation(rawRes = R.raw.success_upload)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.upload_successful),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.resetState()
                            setTitle("")
                            setAuthor("")
                            setCoverUrl("")
                            isInputMetadataBottomSheetExpanded = false
                        }
                    ) {
                        Text(text = stringResource(R.string.upload_another_book))
                    }
                }

                is UploadBookState.Error -> {
                    LottieAnimation(rawRes = R.raw.failed_upload)

                    val text = stringResource(R.string.loading_error)

                    Text(
                        text,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = {
                        viewModel.resetState()
                        setTitle("")
                        setAuthor("")
                        setCoverUrl("")
                        isInputMetadataBottomSheetExpanded = false
                    }) {
                        Text(text = stringResource(R.string.try_again))
                    }
                }
            }
        }
    }

    if (isInputMetadataBottomSheetExpanded && state is UploadBookState.Idle) {
        InputMetadataBottomSheet(
            selectedFileName = fileName,
            onUploadClick = {
                if (title.isEmpty()) {
                    Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                    return@InputMetadataBottomSheet
                }

                if (author.isEmpty()) {
                    Toast.makeText(context, "Author cannot be empty", Toast.LENGTH_SHORT).show()
                    return@InputMetadataBottomSheet
                }

                fileUri?.let { fileUri ->
                    viewModel.uploadBookInBackground(
                        context,
                        fileUri,
                        title,
                        author,
                        coverUrl.takeIf { it.isNotBlank() }
                    )
                    isInputMetadataBottomSheetExpanded = false
                }
            },
            onDismissRequest = {
                isInputMetadataBottomSheetExpanded = false
            },
            title = title,
            onTitleChange = setTitle,
            author = author,
            onAuthorChange = setAuthor,
            coverUrl = coverUrl,
            onCoverUrlChange = setCoverUrl,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputMetadataBottomSheet(
    title: String,
    onTitleChange: (String) -> Unit,
    author: String,
    onAuthorChange: (String) -> Unit,
    coverUrl: String,
    onCoverUrlChange: (String) -> Unit,
    selectedFileName: String,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit,
    onUploadClick: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            val text = stringResource(R.string.select_file) + ": " + selectedFileName
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            ReadlyTextField(
                value = title,
                onValueChange = { onTitleChange(it) },
                label = stringResource(R.string.book_name),
            )

            Spacer(modifier = Modifier.height(8.dp))

            ReadlyTextField(
                value = author,
                onValueChange = { onAuthorChange(it) },
                label = stringResource(R.string.author),
            )

            Spacer(modifier = Modifier.height(8.dp))

            ReadlyTextField(
                value = coverUrl,
                onValueChange = { onCoverUrlChange(it) },
                label = stringResource(R.string.cover_url_optional),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onUploadClick() },
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.upload_book))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun InputMetadataBottomSheetPreview() {
    ReadlyBookTheme {
        InputMetadataBottomSheet(
            title = "",
            onTitleChange = { },
            author = "",
            onAuthorChange = { },
            coverUrl = "",
            onCoverUrlChange = { },
            selectedFileName = "example_book.pdf",
            onDismissRequest = { },
            onUploadClick = { },
        )
    }
}

@Preview
@Composable
private fun UploadBookScreenPreview() {
    ReadlyBookTheme {
        UploadBookScreen()
    }
}

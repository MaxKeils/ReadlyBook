package max.keils.readlybook.ui.screen.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import max.keils.domain.entity.BookContent
import max.keils.domain.entity.ReaderTheme
import max.keils.readlybook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReaderScreen(
    bookId: String,
    bookTitle: String,
    userId: String,
    viewModel: ReaderViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId, userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = bookTitle,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (state is ReaderState.Success) {
                        IconButton(onClick = { viewModel.toggleSettingsVisibility() }) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = stringResource(R.string.display_settings)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is ReaderState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ReaderState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ReaderContent(
                            state = currentState,
                            onPositionChanged = { index, offset ->
                                android.util.Log.d("ReaderScreen", "Position changed - index: $index, offset: $offset")
                                viewModel.updateReadingPosition(index, offset)
                            },
                            onPageChanged = { viewModel.updateCurrentPage(it) },
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(bottom = 24.dp)
                        ) {
                            val isPdf = currentState.bookContent is BookContent.Pdf
                            ProgressIndicator(
                                progress = currentState.progress.progressPercentage,
                                scrollLabel = buildString {
                                    if (isPdf) {
                                        append("Страница ${currentState.progress.currentPage + 1}")
                                        append(" / ")
                                        append(currentState.progress.totalPages.coerceAtLeast(1))
                                    } else {
                                        append(currentState.progress.currentPosition + 1)
                                        append(" / ")
                                        append((currentState.progress.totalSize).coerceAtLeast(1))
                                    }
                                },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }

                    if (currentState.isSettingsVisible) {
                        ReaderSettingsBottomSheet(
                            settings = currentState.settings,
                            onDismiss = { viewModel.toggleSettingsVisibility() },
                            onFontSizeChange = { viewModel.updateFontSize(it) },
                            onLineSpacingChange = { viewModel.updateLineSpacing(it) },
                            onThemeChange = { viewModel.updateTheme(it) }
                        )
                    }
                }

                is ReaderState.Error -> {

                    ErrorDialog(
                        message = currentState.message,
                        onDismiss = onNavigateBack,
                        onDeleteEverywhere = {
                            viewModel.deleteBookEverywhere(currentState.bookId) {
                                onNavigateBack()
                            }
                        },
                        onDeleteLocally = {
                            viewModel.deleteBook(currentState.bookId) {
                                onNavigateBack()
                            }
                        },
                        onRetry = { viewModel.loadBook(bookId, userId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReaderContent(
    state: ReaderState.Success,
    onPositionChanged: (Int, Int) -> Unit,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (state.settings.theme) {
        ReaderTheme.LIGHT -> Color.White
        ReaderTheme.DARK -> Color(0xFF1A1A1A)
        ReaderTheme.SEPIA -> Color(0xFFF4ECD8)
        ReaderTheme.SYSTEM -> MaterialTheme.colorScheme.background
    }

    val textColor = when (state.settings.theme) {
        ReaderTheme.LIGHT -> Color.Black
        ReaderTheme.DARK -> Color(0xFFE0E0E0)
        ReaderTheme.SEPIA -> Color(0xFF5F4B32)
        ReaderTheme.SYSTEM -> MaterialTheme.colorScheme.onBackground
    }

    when (val content = state.bookContent) {
        is BookContent.Text -> {
            TextReaderContent(
                text = content.content,
                settings = state.settings,
                initialPosition = state.progress.currentPosition,
                initialOffset = state.progress.currentOffset,
                backgroundColor = backgroundColor,
                textColor = textColor,
                onPositionChanged = onPositionChanged,
                modifier = modifier.fillMaxSize()
            )
        }

        is BookContent.Epub -> {
            EpubReaderContent(
                chapters = content.chapters,
                settings = state.settings,
                initialPosition = state.progress.currentPosition,
                initialOffset = state.progress.currentOffset,
                backgroundColor = backgroundColor,
                textColor = textColor,
                onPositionChanged = onPositionChanged,
                modifier = modifier.fillMaxSize()
            )
        }

        is BookContent.Pdf -> {
            PdfReaderContent(
                filePath = content.filePath,
                initialPage = state.progress.currentPage,
                onPageChanged = onPageChanged,
                modifier = modifier.fillMaxSize()
            )
        }

        is BookContent.Error -> { }
    }
}

@Composable
private fun TextReaderContent(
    text: String,
    settings: max.keils.domain.entity.ReaderSettings,
    initialPosition: Int,
    initialOffset: Int,
    backgroundColor: Color,
    textColor: Color,
    onPositionChanged: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialPosition,
        initialFirstVisibleItemScrollOffset = initialOffset
    )

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        onPositionChanged(
            listState.firstVisibleItemIndex,
            listState.firstVisibleItemScrollOffset
        )
    }

    LazyColumn(
        state = listState,
        modifier = modifier
             .background(backgroundColor)
             .padding(horizontal = 16.dp)
    ) {
        val chunks = text.chunked(TEXT_CHUNK_SIZE)
        items(chunks.size) { index ->
            Text(
                text = chunks[index],
                fontSize = settings.fontSize.value.sp,
                lineHeight = (settings.fontSize.value * settings.lineSpacing.value).sp,
                color = textColor,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun EpubReaderContent(
    chapters: List<max.keils.domain.entity.Chapter>,
    settings: max.keils.domain.entity.ReaderSettings,
    initialPosition: Int,
    initialOffset: Int,
    backgroundColor: Color,
    textColor: Color,
    onPositionChanged: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialPosition.coerceAtLeast(0),
        initialFirstVisibleItemScrollOffset = initialOffset.coerceAtLeast(0)
    )

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        onPositionChanged(
            listState.firstVisibleItemIndex,
            listState.firstVisibleItemScrollOffset
        )
    }

    LazyColumn(
        state = listState,
        modifier = modifier
             .background(backgroundColor)
             .padding(horizontal = 16.dp)
    ) {
        // Используем items() с индексами для правильной работы
        chapters.forEachIndexed { index, chapter ->
            // Заголовок главы
            item(key = "title_$index") {
                Text(
                    text = chapter.title,
                    fontSize = (settings.fontSize.value + 4).sp,
                    lineHeight = ((settings.fontSize.value + 4) * settings.lineSpacing.value).sp,
                    color = textColor,
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            // Контент главы
            item(key = "content_$index") {
                Text(
                    text = chapter.content,
                    fontSize = settings.fontSize.value.sp,
                    lineHeight = (settings.fontSize.value * settings.lineSpacing.value).sp,
                    color = textColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PdfReaderContent(
    filePath: String,
    initialPage: Int,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    PdfReaderView(
        filePath = filePath,
        initialPage = initialPage,
        onPageChanged = onPageChanged,
        modifier = modifier
    )
}

@Composable
private fun ProgressIndicator(
    progress: Int,
    scrollLabel: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "Прочитано: $progress%",
                style = MaterialTheme.typography.bodySmall
            )
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = scrollLabel,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
    onDeleteEverywhere: () -> Unit,
    onDeleteLocally: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(text = stringResource(R.string.error_loading_book))
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.try_again))
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = onDeleteLocally) {
                    Text(stringResource(R.string.delete_locally))
                }
                TextButton(onClick = onDeleteEverywhere) {
                    Text(
                        text = stringResource(R.string.delete_everywhere),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}

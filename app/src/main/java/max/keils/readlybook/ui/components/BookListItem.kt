package max.keils.readlybook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import max.keils.domain.entity.Book
import max.keils.readlybook.R
import max.keils.readlybook.ui.theme.ReadlyBookTheme

@Composable
internal fun BookListItem(
    book: Book,
    isDownloading: Boolean = false,
    onDeleteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBookClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onBookClick
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BookCover(
                coverUrl = book.coverUrl,
                modifier = Modifier.size(width = 60.dp, height = 80.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .sizeIn(48.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isDownloading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    book.isAvailableOffline -> {
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }

                    else -> {
                        IconButton(onClick = onDownloadClick) {
                            Icon(
                                Icons.Default.CloudDownload,
                                contentDescription = stringResource(R.string.download)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookCover(
    coverUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (coverUrl.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = stringResource(R.string.book_cover_placeholder),
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            AsyncImage(
                model = coverUrl,
                contentDescription = "Book cover",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview
@Composable
private fun BookListItemNotAvailablePreview() {
    ReadlyBookTheme {
        BookListItem(
            book = Book(
                id = "1",
                title = "Sample Book Title",
                author = "Author Name",
                fileUrl = "",
                userId = "",
                fileName = "",
                localPath = "",
                uploadedAt = 1
            ),
            isDownloading = false,
            onDeleteClick = {},
            onDownloadClick = {},
            onBookClick = {}
        )
    }
}

@Preview
@Composable
private fun BookListItemAvailablePreview() {
    ReadlyBookTheme {
        BookListItem(
            book = Book(
                id = "1",
                title = "Sample Book Title",
                author = "Author Name",
                fileUrl = "",
                userId = "",
                fileName = "",
                localPath = "1",
                uploadedAt = 1
            ),
            isDownloading = false,
            onDeleteClick = {},
            onDownloadClick = {},
            onBookClick = {}
        )
    }
}

@Preview
@Composable
private fun BookListItemDownloadingPreview() {
    ReadlyBookTheme {
        BookListItem(
            book = Book(
                id = "1",
                title = "Sample Book Title",
                author = "Author Name",
                fileUrl = "",
                userId = "",
                fileName = "",
                localPath = "",
                uploadedAt = 1
            ),
            isDownloading = true,
            onDeleteClick = {},
            onDownloadClick = {},
            onBookClick = {}
        )
    }
}

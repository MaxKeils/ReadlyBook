package max.keils.readlybook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import max.keils.domain.entity.Book
import max.keils.readlybook.ui.theme.ReadlyBookTheme

@Composable
internal fun BookListItem(
    book: Book,
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
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium)
                Text(book.author, style = MaterialTheme.typography.bodyMedium)
            }

            if (book.isAvailableOffline) {
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            } else {
                IconButton(onClick = onDownloadClick) {
                    Icon(Icons.Default.CloudDownload, contentDescription = "Скачать")
                }
            }
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
            onDeleteClick = {},
            onDownloadClick = {},
            onBookClick = {}
        )
    }
}
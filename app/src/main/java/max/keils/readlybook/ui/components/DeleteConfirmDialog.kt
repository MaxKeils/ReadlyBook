package max.keils.readlybook.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import max.keils.readlybook.R

@Composable
fun DeleteConfirmDialog(
    bookTitle: String,
    onDismiss: () -> Unit,
    onDeleteLocal: () -> Unit,
    onDeleteLocalAndServer: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_book)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.delete, bookTitle),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.select_an_action),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Row {
                TextButton(onClick = onDeleteLocal) {
                    Text(stringResource(R.string.only_locally))
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDeleteLocalAndServer) {
                    Text(stringResource(R.string.everywhere))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview
@Composable
private fun DeleteConfirmDialogPreview() {
    DeleteConfirmDialog(
        bookTitle = "Sample Book",
        onDismiss = {},
        onDeleteLocal = {},
        onDeleteLocalAndServer = {}
    )
}


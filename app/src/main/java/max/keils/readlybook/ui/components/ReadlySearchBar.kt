package max.keils.readlybook.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import max.keils.readlybook.R
import max.keils.readlybook.ui.theme.ReadlyBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReadlySearchBar(
    searchBarState: SearchBarState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        state = searchBarState, inputField = {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(stringResource(R.string.search_books)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ReadlySearchBarPreview() {
    ReadlyBookTheme {
        val state = rememberSearchBarState()

        ReadlySearchBar(
            searchBarState = state,
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}
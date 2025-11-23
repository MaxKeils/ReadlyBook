package max.keils.readlybook.ui.screen.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import max.keils.domain.entity.FontSize
import max.keils.domain.entity.LineSpacing
import max.keils.domain.entity.ReaderSettings
import max.keils.domain.entity.ReaderTheme
import max.keils.readlybook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReaderSettingsBottomSheet(
    settings: ReaderSettings,
    onDismiss: () -> Unit,
    onFontSizeChange: (FontSize) -> Unit,
    onLineSpacingChange: (LineSpacing) -> Unit,
    onThemeChange: (ReaderTheme) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.display_settings),
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.font_size),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FontSizeSelector(
                selectedSize = settings.fontSize,
                onSizeSelected = onFontSizeChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.line_spacing),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LineSpacingSelector(
                selectedSpacing = settings.lineSpacing,
                onSpacingSelected = onLineSpacingChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.color_theme),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            ThemeSelector(
                selectedTheme = settings.theme,
                onThemeSelected = onThemeChange
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FontSizeSelector(
    selectedSize: FontSize,
    onSizeSelected: (FontSize) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FontSize.entries.forEach { size ->
            FilterChip(
                selected = selectedSize == size,
                onClick = { onSizeSelected(size) },
                label = {
                    Text(
                        text = when (size) {
                            FontSize.SMALL -> stringResource(R.string.small)
                            FontSize.MEDIUM -> stringResource(R.string.medium)
                            FontSize.LARGE -> stringResource(R.string.large)
                            FontSize.EXTRA_LARGE -> stringResource(R.string.extra_large)
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun LineSpacingSelector(
    selectedSpacing: LineSpacing,
    onSpacingSelected: (LineSpacing) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LineSpacing.entries.forEach { spacing ->
            FilterChip(
                selected = selectedSpacing == spacing,
                onClick = { onSpacingSelected(spacing) },
                label = {
                    Text(
                        text = when (spacing) {
                            LineSpacing.COMPACT -> stringResource(R.string.compact)
                            LineSpacing.NORMAL -> stringResource(R.string.normal)
                            LineSpacing.RELAXED -> stringResource(R.string.relaxed)
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: ReaderTheme,
    onThemeSelected: (ReaderTheme) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ReaderTheme.entries.forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeSelected(theme) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedTheme == theme,
                    onClick = { onThemeSelected(theme) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (theme) {
                        ReaderTheme.LIGHT -> stringResource(R.string.light)
                        ReaderTheme.DARK -> stringResource(R.string.dark)
                        ReaderTheme.SEPIA -> stringResource(R.string.sepia)
                        ReaderTheme.SYSTEM -> stringResource(R.string.system)
                    }
                )
            }
        }
    }
}


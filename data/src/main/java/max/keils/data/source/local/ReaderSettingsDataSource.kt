package max.keils.data.source.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import max.keils.domain.entity.FontSize
import max.keils.domain.entity.LineSpacing
import max.keils.domain.entity.ReaderSettings
import max.keils.domain.entity.ReaderTheme
import javax.inject.Inject

class ReaderSettingsDataSource @Inject constructor(
    context: Application
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getSettings(): ReaderSettings {
        val fontSizeName = prefs.getString(KEY_FONT_SIZE, FontSize.MEDIUM.name)
            ?: FontSize.MEDIUM.name
        val lineSpacingName = prefs.getString(KEY_LINE_SPACING, LineSpacing.NORMAL.name)
            ?: LineSpacing.NORMAL.name
        val themeName = prefs.getString(KEY_THEME, ReaderTheme.SYSTEM.name)
            ?: ReaderTheme.SYSTEM.name

        return ReaderSettings(
            fontSize = FontSize.valueOf(fontSizeName),
            lineSpacing = LineSpacing.valueOf(lineSpacingName),
            theme = ReaderTheme.valueOf(themeName)
        )
    }

    fun saveSettings(settings: ReaderSettings) {
        prefs.edit().apply {
            putString(KEY_FONT_SIZE, settings.fontSize.name)
            putString(KEY_LINE_SPACING, settings.lineSpacing.name)
            putString(KEY_THEME, settings.theme.name)
            apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "reader_settings"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_LINE_SPACING = "line_spacing"
        private const val KEY_THEME = "theme"
    }
}


package max.keils.domain.entity

data class ReaderSettings(
    val fontSize: FontSize = FontSize.MEDIUM,
    val lineSpacing: LineSpacing = LineSpacing.NORMAL,
    val theme: ReaderTheme = ReaderTheme.SYSTEM
)

enum class FontSize(val value: Float) {
    SMALL(14f),
    MEDIUM(18f),
    LARGE(22f),
    EXTRA_LARGE(26f)
}

enum class LineSpacing(val value: Float) {
    COMPACT(1.2f),
    NORMAL(1.5f),
    RELAXED(2.0f)
}

enum class ReaderTheme {
    LIGHT,
    DARK,
    SEPIA,
    SYSTEM
}


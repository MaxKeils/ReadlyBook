package max.keils.domain.entity

data class ReadingProgress(
    val bookId: String,
    val currentPosition: Int = 0,
    val totalSize: Int = 0,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val currentOffset: Int = 0,
    val lastReadTimestamp: Long = System.currentTimeMillis()
) {
    val progressPercentage: Int
        get() = when {
            totalSize > 1 -> {
                val clamped = currentPosition.coerceIn(0, totalSize - 1)
                val p = clamped.toFloat() / (totalSize - 1).toFloat() * 100f
                p.toInt().coerceIn(0, 100)
            }
            totalPages > 1 -> {
                val clamped = currentPage.coerceIn(0, totalPages - 1)
                val p = clamped.toFloat() / (totalPages - 1).toFloat() * 100f
                p.toInt().coerceIn(0, 100)
            }
            else -> 0
        }
}

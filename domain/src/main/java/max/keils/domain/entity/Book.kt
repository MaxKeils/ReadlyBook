package max.keils.domain.entity

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val fileUrl: String,
    val userId: String,
    val fileName: String = "",
    val localPath: String? = null,
    val uploadedAt: Long = System.currentTimeMillis()
) {
    val isAvailableOffline: Boolean = !localPath.isNullOrEmpty()
}

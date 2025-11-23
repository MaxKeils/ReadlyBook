package max.keils.domain.entity

sealed class BookContent {
    data class Text(val content: String) : BookContent()
    data class Epub(val chapters: List<Chapter>) : BookContent()
    data class Pdf(val filePath: String, val pageCount: Int) : BookContent()
    data class Error(val message: String) : BookContent()
}

data class Chapter(
    val title: String,
    val content: String
)


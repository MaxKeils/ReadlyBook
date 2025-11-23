package max.keils.data.source.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import max.keils.domain.entity.BookContent
import max.keils.domain.entity.Chapter
import java.io.File
import java.util.zip.ZipFile
import javax.inject.Inject

class BookContentReader @Inject constructor() {

    suspend fun readBook(localPath: String): BookContent = withContext(Dispatchers.IO) {
        try {
            val file = File(localPath)

            if (!file.exists()) {
                return@withContext BookContent.Error("File not found: $localPath")
            }

            when (file.extension.lowercase()) {
                "txt" -> readTextBook(file)
                "epub" -> readEpubBook(file)
                "pdf" -> readPdfBook(file)
                else -> BookContent.Error("Unsupported format:Unsupported format: ${file.extension}")
            }
        } catch (e: Exception) {
            BookContent.Error("Error reading a book: ${e.message}")
        }
    }

    private fun readTextBook(file: File): BookContent {
        return try {
            val content = file.readText(Charsets.UTF_8)
            BookContent.Text(content)
        } catch (_: Exception) {
            try {
                val content = file.readText(Charsets.ISO_8859_1)
                BookContent.Text(content)
            } catch (e2: Exception) {
                BookContent.Error("Couldn't read the text file: ${e2.message}")
            }
        }
    }

    private fun readEpubBook(file: File): BookContent {
        return try {
            val chapters = mutableListOf<Chapter>()

            ZipFile(file).use { zip ->
                val entries = zip.entries().toList()
                    .filter { entry ->
                        val name = entry.name.lowercase()
                        !entry.isDirectory &&
                        (name.endsWith(".html") || name.endsWith(".xhtml") || name.endsWith(".htm"))
                    }
                    .sortedBy { it.name }

                entries.forEachIndexed { index, entry ->
                    try {
                        val inputStream = zip.getInputStream(entry)
                        val content = inputStream.bufferedReader(Charsets.UTF_8).readText()

                        // Извлекаем текст из HTML
                        val text = content
                            .replace("<[^>]*>".toRegex(), "") // Убираем HTML теги
                            .replace("&nbsp;", " ")
                            .replace("&quot;", "\"")
                            .replace("&amp;", "&")
                            .replace("&lt;", "<")
                            .replace("&gt;", ">")
                            .replace("&apos;", "'")
                            .replace("\\s+".toRegex(), " ") // Множественные пробелы в один
                            .trim()

                        if (text.isNotEmpty() && text.length > 50) { // Пропускаем слишком короткие
                            // Пытаемся извлечь заголовок из имени файла
                            val fileName = entry.name.substringAfterLast('/')
                                .substringBeforeLast('.')
                            val title = if (fileName.matches(Regex("chapter\\d+", RegexOption.IGNORE_CASE))) {
                                "Глава ${index + 1}"
                            } else {
                                fileName.replace("[_-]".toRegex(), " ")
                                    .replaceFirstChar { it.uppercase() }
                            }

                            chapters.add(Chapter(title, text))
                        }

                        inputStream.close()
                    } catch (_: Exception) { }
                }
            }

            if (chapters.isEmpty()) {
                BookContent.Error("The EPUB file does not contain any text")
            } else {
                BookContent.Epub(chapters)
            }
        } catch (e: Exception) {
            BookContent.Error("EPUB reading error: ${e.message}")
        }
    }

    private fun readPdfBook(file: File): BookContent {
        return try {
            BookContent.Pdf(file.absolutePath, 0)
        } catch (e: Exception) {
            BookContent.Error("PDF reading error: ${e.message}")
        }
    }
}




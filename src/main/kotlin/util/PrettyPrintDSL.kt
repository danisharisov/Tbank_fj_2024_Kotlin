import dto.News
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.text.StringBuilder

@DslMarker
annotation class PrettyPrintDslMarker

@PrettyPrintDslMarker
class PrettyPrintBuilder {
    private val content = StringBuilder()

    fun header(level: Int, block: PrettyPrintBuilder.() -> Unit) {
        append("#".repeat(level) + " ")
        block()
    }

    fun text(block: PrettyPrintBuilder.() -> Unit) {
        block()
    }

    fun append(text: String) {
        content.append(text)
    }

    fun build(): String = content.toString()
}

private val logger = LoggerFactory.getLogger("PrettyPrintLogger")

fun prettyPrintNews(news: List<News>): String {
    val builder = PrettyPrintBuilder()
    builder.header(level = 1) { builder.append("News Report ") }
    builder.header(level = 2) { builder.append("Detailed News") }
    builder.append("\n------------\n")

    news.forEach { article ->
        builder.text {
            builder.append("Title: ${article.title}\n")
            builder.append("Place: ${article.place?.title ?: "Unknown"}\n")
            builder.append("Description: ${article.description ?: "No description"}\n")
            builder.append("Site URL: ${article.siteUrl ?: "No URL"}\n")
            builder.append("Favorites Count: ${article.favoritesCount}\n")
            builder.append("Comments Count: ${article.commentsCount}\n")
            builder.append("Publication Date: ${article.publicationDate}\n")
            builder.append("Rating: ${article.rating ?: "Unknown"}\n")
            builder.append("------------\n")
        }
    }

    val prettyPrintContent = builder.build()
    println(prettyPrintContent)
    return prettyPrintContent
}

fun savePrettyNews(news: List<News>, basePath: String) {
    val content = prettyPrintNews(news)

    var filePath = basePath
    var fileNumber = 1

    while (File(filePath).exists()) {
        filePath = "$basePath-$fileNumber"
        fileNumber++
    }

    try {
        File(filePath).bufferedWriter().use { writer ->
            writer.write(content)
        }
        logger.info("Pretty printed news saved to file $filePath")
    } catch (e: Exception) {
        logger.error("Failed to save pretty printed news to file $filePath", e)
    }
}
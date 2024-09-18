import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import mu.KotlinLogging
import service.*
import util.*

private val logger = KotlinLogging.logger {}

fun main() = runBlocking {
    val newsService = NewsService()

    try {
        logger.info("Starting to fetch news")
        val news = newsService.getNews()
        logger.info("Fetched ${news.size} news items")
        val newsProcessor = NewsProcessor()
        val filteredNews = newsProcessor.getMostRatedNews(news, 50, LocalDate.now())
        logger.info("Filtered to ${filteredNews.size} most rated news items")

        newsService.saveNews("news.csv", filteredNews)
        savePrettyNews(filteredNews, "PrettyPrintedNews.txt")

    } catch (e: Exception) {
        logger.error("An error occurred while processing news", e)
    }
}




import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import mu.KotlinLogging
import service.*
import util.*

private val logger = KotlinLogging.logger {}

fun main() = runBlocking {
    val newsService = NewsService()
    val parallelFetcher = ParallelNewsFetcherWithWorker(newsService)

    try {
        val startParallel = System.currentTimeMillis()
        logger.info("Starting to fetch news in parallel")

        parallelFetcher.fetchAndSaveParallel(this, "parallel_news.csv")

        val endParallel = System.currentTimeMillis()
        logger.info("Время выполнения многопоточной обработки: ${endParallel - startParallel} ms")

        val startSequential = System.currentTimeMillis()
        logger.info("Starting to fetch news sequentially")
        val news = newsService.getNews()
        val newsProcessor = NewsProcessor()
        val filteredNews = newsProcessor.getMostRatedNews(news, 50, LocalDate.now())

        newsService.saveNews("news.csv", filteredNews)
        val endSequential = System.currentTimeMillis()
        logger.info("Время выполнения однопоточной обработки: ${endSequential - startSequential} ms")
        savePrettyNews(filteredNews, "PrettyPrintedNews.txt")

    } catch (e: Exception) {
        logger.error("An error occurred while processing news", e)
    }
}




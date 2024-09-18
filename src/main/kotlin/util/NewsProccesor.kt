package util
import dto.News
import java.time.LocalDate
import kotlin.math.exp

class NewsProcessor {
    fun getMostRatedNews(news: List<News>, count: Int, endDate: LocalDate): List<News> {
        return news.filter { it.publicationDate <= endDate }
            .onEach { it.rating = 1 / (1 + exp(-(it.favoritesCount.toDouble() / (it.commentsCount + 1)))) }
            .sortedByDescending { it.rating }
            .take(count)
    }
}
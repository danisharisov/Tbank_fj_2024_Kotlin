package service

import dto.News
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import mu.KotlinLogging
import java.io.File

const val BASE_URL = "https://kudago.com/public-api/v1.4/news/"
private val logger = KotlinLogging.logger {}

class NewsService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getNews(count: Int = 100): List<News> {
        val pageSize = 100
        val totalPages = (count + pageSize - 1) / pageSize
        val newsList = mutableListOf<News>()

        for (page in 1..totalPages) {
            logger.info { "Fetching page $page" }
            try {
                val response = client.get(BASE_URL) {
                    parameter("location", "kzn")
                    parameter("text_format", "text")
                    parameter("expand", "place")
                    parameter(
                        "fields",
                        "id,title,place,description,site_url,favorites_count,comments_count,publication_date"
                    )
                    parameter("page_size", pageSize)
                    parameter("page", page)
                    parameter("order_by", "-publication_date")
                }

                val jsonResponse = response.body<JsonObject>()
                val newsArray = jsonResponse["results"] as? JsonArray ?: continue

                newsArray.forEach { jsonElement ->
                    val news = Json.decodeFromJsonElement<News>(jsonElement)
                    newsList.add(news)
                }

                if (newsList.size >= count) break
            } catch (e: Exception) {
                logger.error(e) { "Failed to fetch news for page $page" }
            }
        }

        return newsList.take(count)
    }

    fun saveNews(path: String, news: Collection<News>) {
        var filePath = path
        var fileNumber = 1

        while (File(filePath).exists()) {
            filePath = "$path-$fileNumber"
            fileNumber++
        }

        try {
            File(filePath).bufferedWriter().use { writer ->
                writer.write("id,title,place,description,siteUrl,favoritesCount,commentsCount,publicationDate,rating\n")
                news.forEach {
                    writer.write("${it.id},${it.title},${it.place?.title ?: "Неизвестно"},${it.description ?: "Неизвестно"},${it.siteUrl ?: "Неизвестно"},${it.favoritesCount},${it.commentsCount},${it.publicationDate},${it.rating}\n")
                }
            }

            logger.info { "News saved successfully to $filePath" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to save news to $filePath" }
        }
    }
}


package service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import dto.News


class ParallelNewsFetcherWithWorker(private val newsService: NewsService) {

    private val workerDispatcher = newFixedThreadPoolContext(nThreads = 4, name = "WorkerPool")
    private val newsChannel = Channel<List<News>>(Channel.UNLIMITED)

    suspend fun fetchAndSaveParallel(scope: CoroutineScope, filePath: String) = withContext(workerDispatcher) {
        repeat(4) { workerId ->
            scope.launch {
                val page = workerId + 1
                val news = newsService.getNews(page)
                newsChannel.send(news)
            }
        }

        scope.launch {
            for (newsList in newsChannel) {
                newsService.saveNews(filePath, newsList)
            }
            newsChannel.close()
        }
    }
}


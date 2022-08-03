package com.alexmurz.topic.service

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.api.TopicAPI
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

private class ListTopicsStorage(
    list: List<Topic>
) : TopicAPI.LoadDown,
    TopicAPI.LoadNewest,
    TopicAPI.LoadUp,
    TopicAPI.SaveTopics {
    val list = list.sortedByDescending { it.date }.toMutableList()

    override suspend fun loadNewestTopics(limit: Int): Set<Topic> {
        return list.asSequence().take(limit).toSet()
    }

    override suspend fun loadDownTopics(date: CATime, limit: Int): Set<Topic> {
        return list.asSequence()
            .filter { it.date < date }
            .take(limit)
            .toSet()
    }

    override suspend fun loadUpTopics(date: CATime, limit: Int): Set<Topic> {
        return list.asSequence()
            .filter { it.date > date }
            .toList()
            .takeLast(limit)
            .toSet()
    }

    override suspend fun saveTopics(topics: Set<Topic>) {
        list.addAll(topics)
        list.sortByDescending { it.date }
    }
}

private object StubTopicsStorage :
    TopicAPI.LoadNewest,
    TopicAPI.LoadDown,
    TopicAPI.LoadUp,
    TopicAPI.SaveTopics {

    override suspend fun loadNewestTopics(limit: Int): Set<Topic> {
        throw UnsupportedOperationException()
    }

    override suspend fun loadDownTopics(date: CATime, limit: Int): Set<Topic> {
        throw UnsupportedOperationException()
    }

    override suspend fun loadUpTopics(date: CATime, limit: Int): Set<Topic> {
        throw UnsupportedOperationException()
    }

    override suspend fun saveTopics(topics: Set<Topic>) {
        throw UnsupportedOperationException()
    }

}

internal class TopicServiceTest {

    private fun topic(id: Int) = Topic(
        id = id.toLong(),
        date = CATime.of(1_000_000L - id.toLong()),
        title = "Title $id",
        message = "",
        attachments = emptyList()
    )

    @Test
    fun `should load topics from database`() = runBlocking {
        val limit = 5

        val topicsList = (0 until limit * 3).mapTo(mutableListOf(), ::topic)

        val remote = ListTopicsStorage(emptyList())
        val local = ListTopicsStorage(topicsList)

        val service = TopicService(
            remoteLoadDown = remote,
            remoteLoadUp = remote,
            remoteLoadNewest = remote,
            localLoadDownPage = local,
            localLoadNewest = local,
            localSaveTopics = local,
        )
        val context = service.createNewContext(limit)
        Assert.assertEquals(topicsList.take(limit).toSet(), context.topics)

        val secondPack = service.loadMoreTopics(context)
        Assert.assertEquals(topicsList.drop(limit).take(limit).toSet(), secondPack)
        Assert.assertEquals(topicsList.take(limit * 2).toSet(), context.topics)

        val thirdPack = service.loadMoreTopics(context)
        Assert.assertEquals(topicsList.drop(limit * 2).take(limit).toSet(), thirdPack)
        Assert.assertEquals(topicsList.take(limit * 3).toSet(), context.topics)
    }

    @Test
    fun `should save topics from network to database`() = runBlocking {
        val limit = 5

        val topicsList = (0 until limit * 3).mapTo(mutableListOf(), ::topic)

        val remote = ListTopicsStorage(topicsList)
        val local = ListTopicsStorage(emptyList())

        val service = TopicService(
            remoteLoadDown = remote,
            remoteLoadUp = remote,
            remoteLoadNewest = remote,
            localLoadDownPage = local,
            localLoadNewest = local,
            localSaveTopics = local,
        )
        val context = service.createNewContext(limit)

        Assert.assertEquals(topicsList.take(limit).toSet(), context.topics)
        Assert.assertEquals(topicsList.take(limit).toSet(), local.list.toSet())

        Assert.assertEquals(
            topicsList.drop(limit).take(limit).toSet(),
            service.loadMoreTopics(context)
        )
        Assert.assertEquals(topicsList.take(limit * 2).toSet(), context.topics)
        Assert.assertEquals(topicsList.take(limit * 2).toSet(), local.list.toSet())

        Assert.assertEquals(
            topicsList.drop(limit * 2).take(limit).toSet(),
            service.loadMoreTopics(context)
        )
        Assert.assertEquals(topicsList.take(limit * 3).toSet(), context.topics)
        Assert.assertEquals(topicsList.take(limit * 3).toSet(), local.list.toSet())
    }

    @Test
    fun `should save to database then restore from it`() = runBlocking {

        val limit = 5

        val topicsList = (0 until limit * 3).mapTo(mutableListOf(), ::topic)

        val remote = ListTopicsStorage(topicsList)
        val local = ListTopicsStorage(emptyList())

        var service = TopicService(
            remoteLoadDown = remote,
            remoteLoadUp = remote,
            remoteLoadNewest = remote,
            localLoadDownPage = local,
            localLoadNewest = local,
            localSaveTopics = local,
        )
        var context = service.createNewContext(limit)
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)

        // Reset service and context, keeping localTopics instance as database
        service = TopicService(
            remoteLoadDown = StubTopicsStorage,
            remoteLoadUp = StubTopicsStorage,
            remoteLoadNewest = StubTopicsStorage,
            localLoadDownPage = local,
            localLoadNewest = local,
            localSaveTopics = local,
        )
        context = service.createNewContext(limit)

        Assert.assertEquals(topicsList.take(limit).toSet(), context.topics)

        Assert.assertEquals(
            topicsList.drop(limit).take(limit).toSet(),
            service.loadMoreTopics(context)
        )
        Assert.assertEquals(topicsList.take(limit * 2).toSet(), context.topics)

        Assert.assertEquals(
            topicsList.drop(limit * 2).take(limit).toSet(),
            service.loadMoreTopics(context)
        )
        Assert.assertEquals(topicsList.take(limit * 3).toSet(), context.topics)
    }

    @Test
    fun `should load up from network`(): Unit = runBlocking {
        val limit = 5

        val allTopics = (0 until limit * 10).mapTo(mutableListOf(), ::topic)
        val currentTopics = allTopics.drop(limit * 5)

        val remote = ListTopicsStorage(currentTopics)
        val local = ListTopicsStorage(emptyList())

        val service = TopicService(
            remoteLoadDown = remote,
            remoteLoadUp = remote,
            remoteLoadNewest = remote,
            localLoadDownPage = local,
            localLoadNewest = local,
            localSaveTopics = local,
        )
        val context = service.createNewContext(limit)
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)
        Assert.assertEquals(currentTopics.toSet(), context.topics)

        // Update `remote` list with new content
        remote.list.apply {
            clear()
            addAll(allTopics)
            sortByDescending { it.date }
        }

        Assert.assertEquals(allTopics.drop(limit * 5).toSet(), context.topics)

        Assert.assertEquals(
            allTopics.drop(limit * 4).take(limit).toSet(),
            service.updateTopics(context)
        )
        Assert.assertEquals(allTopics.drop(limit * 4).toSet(), context.topics)
        Assert.assertEquals(false, context.upToDate)


        Assert.assertEquals(
            allTopics.drop(limit * 3).take(limit).toSet(),
            service.updateTopics(context)
        )
        Assert.assertEquals(allTopics.drop(limit * 3).toSet(), context.topics)
        Assert.assertEquals(false, context.upToDate)


        Assert.assertEquals(
            allTopics.drop(limit * 2).take(limit).toSet(),
            service.updateTopics(context)
        )
        Assert.assertEquals(allTopics.drop(limit * 2).toSet(), context.topics)
        Assert.assertEquals(false, context.upToDate)


        Assert.assertEquals(
            allTopics.drop(limit).take(limit).toSet(),
            service.updateTopics(context)
        )
        Assert.assertEquals(allTopics.drop(limit).toSet(), context.topics)
        Assert.assertEquals(false, context.upToDate)


        Assert.assertEquals(
            allTopics.take(limit).toSet(),
            service.updateTopics(context)
        )
        Assert.assertEquals(allTopics.toSet(), context.topics)
        Assert.assertEquals(false, context.upToDate)

        Assert.assertEquals(emptySet<Topic>(), service.updateTopics(context))
        Assert.assertEquals(true, context.upToDate)

    }
}
package com.alexmurz.topic.service

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.topic.api.TopicAPI
import com.alexmurz.topic.model.Topic
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

private class ListTopicsStorage(
    private val list: MutableList<Topic>
) : TopicAPI.LoadDown,
    TopicAPI.LoadNewest,
    TopicAPI.LoadUp,
    TopicAPI.SaveTopics,
    TopicAPI.CreateTopic {

    init {
        list.sortByDescending { it.date }
    }

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

    override suspend fun createTopic(title: String, message: String): Topic {
        return Topic(
            id = list.size.toLong(),
            date = CATime.now(),
            title = title,
            message = message,
            attachments = emptyList()
        ).also {
            list.add(0, it)
        }
    }
}

private object StubTopicsStorage :
    TopicAPI.LoadNewest,
    TopicAPI.LoadDown,
    TopicAPI.LoadUp,
    TopicAPI.SaveTopics,
    TopicAPI.CreateTopic {

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

    override suspend fun createTopic(title: String, message: String): Topic {
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

    private fun service(
        remoteList: MutableList<Topic>,
        localList: MutableList<Topic>
    ): TopicService {
        val remote = ListTopicsStorage(remoteList)
        val local = ListTopicsStorage(localList)
        return TopicService(
            remoteLoadDown = remote,
            remoteLoadUp = remote,
            remoteLoadNewest = remote,
            createNewTopic = remote,
            localLoadDownPage = local,
            localLoadNewest = local,
            localSaveTopics = local,
        )
    }

    private fun service(
        localList: MutableList<Topic>
    ): TopicService {
        val local = ListTopicsStorage(localList)
        return TopicService(
            remoteLoadDown = StubTopicsStorage,
            remoteLoadUp = StubTopicsStorage,
            remoteLoadNewest = StubTopicsStorage,
            createNewTopic = StubTopicsStorage,
            localLoadDownPage = local,
            localLoadNewest = local,
            localSaveTopics = local,
        )
    }

    @Test
    fun `should load topics from database`() = runBlocking {
        val limit = 5

        val topicsList = (0 until limit * 3).mapTo(mutableListOf(), ::topic)

        val service = service(mutableListOf(), topicsList)

        val context = service.createNewContext(limit)
        service.initialize(context)

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

        val local = mutableListOf<Topic>()

        val service = service(topicsList, local)
        val context = service.createNewContext(limit)
        service.initialize(context)
        Assert.assertEquals(emptySet<Topic>(), context.topics)

        Assert.assertEquals(topicsList.take(limit).toSet(), service.updateTopics(context))
        Assert.assertEquals(topicsList.take(limit).toSet(), context.topics)
        Assert.assertEquals(topicsList.take(limit).toSet(), local.toSet())

        Assert.assertEquals(
            topicsList.drop(limit).take(limit).toSet(),
            service.loadMoreTopics(context)
        )
        Assert.assertEquals(topicsList.take(limit * 2).toSet(), context.topics)
        Assert.assertEquals(topicsList.take(limit * 2).toSet(), local.toSet())

        Assert.assertEquals(
            topicsList.drop(limit * 2).take(limit).toSet(),
            service.loadMoreTopics(context)
        )
        Assert.assertEquals(topicsList.take(limit * 3).toSet(), context.topics)
        Assert.assertEquals(topicsList.take(limit * 3).toSet(), local.toSet())
    }

    @Test
    fun `should save to database then restore from it`() = runBlocking {

        val limit = 5

        val topicsList = (0 until limit * 3).mapTo(mutableListOf(), ::topic)
        val local = mutableListOf<Topic>()

        var service = service(topicsList, local)
        var context = service.createNewContext(limit)
        service.initialize(context)

        // Load 3 packs to local storage
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)

        // Reset service and context, keeping localTopics instance as database
        service = service(localList = local)
        context = service.createNewContext(limit)
        service.initialize(context)

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

        val remote = currentTopics.toMutableList()
        val local = mutableListOf<Topic>()

        val service = service(remote, local)
        val context = service.createNewContext(limit)
        service.initialize(context)

        // Load 5 packs
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)
        service.loadMoreTopics(context)
        Assert.assertEquals(currentTopics.toSet(), context.topics)

        fun test(
            offset: Int,
            expect: Set<Topic>,
            complete: Boolean
        ) {
            val crop = allTopics.drop(limit * offset)
            Assert.assertEquals(
                crop.take(limit).toSet(),
                expect
            )
            Assert.assertEquals(crop.toSet(), context.topics)
            Assert.assertEquals(complete, context.upToDate)

        }

        // Update `remote` list with new content
        remote.apply {
            clear()
            addAll(allTopics)
            sortByDescending { it.date }
        }

        Assert.assertEquals(allTopics.drop(limit * 5).toSet(), context.topics)

        test(4, service.updateTopics(context), false)
        test(3, service.updateTopics(context), false)
        test(2, service.updateTopics(context), false)
        test(1, service.updateTopics(context), false)
        test(0, service.updateTopics(context), false)

        Assert.assertEquals(emptySet<Topic>(), service.updateTopics(context))
        Assert.assertEquals(true, context.upToDate)

    }
}
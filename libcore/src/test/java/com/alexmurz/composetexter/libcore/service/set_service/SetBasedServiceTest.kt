package com.alexmurz.composetexter.libcore.service.set_service

import com.alexmurz.composetexter.libcore.util.quickCompare
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

private data class TestData(
    val id: Int,
    val date: Int,
) {
    override fun equals(other: Any?) = quickCompare(other) {
        id == it.id
    }

    override fun hashCode(): Int = id.hashCode()
}

private class TestContext(limit: Int) : CommonSetBasedServiceContext<TestData>(limit) {
    override fun getNewerReference(): TestData? = data.maxByOrNull { it.date }
    override fun getOlderReference(): TestData? = data.minByOrNull { it.date }
}

private class TestService(
    private val localLoadNewest: suspend (limit: Int) -> Set<TestData>,
    private val localLoadOlder: suspend (limit: Int, date: Int) -> Set<TestData>,
    private val localSave: suspend (data: Set<TestData>) -> Unit,
    private val remoteLoadNewest: suspend (limit: Int) -> Set<TestData>,
    private val remoteLoadNewer: suspend (limit: Int, date: Int) -> Set<TestData>,
    private val remoteLoadOlder: suspend (limit: Int, date: Int) -> Set<TestData>,
) : CommonSetBasedService<TestData, TestContext>() {
    override suspend fun localLoadNewest(context: TestContext): Set<TestData> =
        localLoadNewest(context.limit)

    override suspend fun localLoadOlder(context: TestContext, reference: TestData): Set<TestData> =
        localLoadOlder(context.limit, reference.date)

    override suspend fun localSave(context: TestContext, data: Set<TestData>) =
        localSave(data)

    override suspend fun remoteLoadNewest(context: TestContext): Set<TestData> =
        remoteLoadNewest(context.limit)

    override suspend fun remoteLoadNewer(context: TestContext, reference: TestData): Set<TestData> =
        remoteLoadNewer(context.limit, reference.date)

    override suspend fun remoteLoadOlder(context: TestContext, reference: TestData): Set<TestData> =
        remoteLoadOlder(context.limit, reference.date)
}

private fun List<TestData>.asLoadNewest(): suspend (Int) -> Set<TestData> {
    return { limit ->
        sortedByDescending { it.date }.take(limit).toSet()
    }
}

private fun List<TestData>.asLoadNewer(): suspend (Int, Int) -> Set<TestData> {
    return { limit, date ->
        sortedByDescending { it.date }.takeWhile { it.date > date }.takeLast(limit).toSet()
    }
}

private fun List<TestData>.asLoadOlder(): suspend (Int, Int) -> Set<TestData> {
    return { limit, date ->
        sortedByDescending { it.date }.dropWhile { it.date >= date }.take(limit).toSet()
    }
}

private fun MutableList<TestData>.asSave(): suspend (Set<TestData>) -> Unit {
    return { data -> addAll(data) }
}

private object Stub :
    suspend (Any?) -> Set<TestData>,
    suspend (Any?, Any?) -> Set<TestData> {
    override suspend fun invoke(p1: Any?): Set<TestData> {
        error("Stub")
    }

    override suspend fun invoke(p1: Any?, p2: Any?): Set<TestData> {
        error("Stub")
    }
}

private fun data(idx: Int) = TestData(
    id = idx,
    date = 1_000_000 - idx,
)

private fun localService(localList: MutableList<TestData>) = TestService(
    localLoadNewest = localList.asLoadNewest(),
    localLoadOlder = localList.asLoadOlder(),
    localSave = localList.asSave(),
    remoteLoadNewest = Stub,
    remoteLoadNewer = Stub,
    remoteLoadOlder = Stub,
)

private fun service(localList: MutableList<TestData>, remoteList: MutableList<TestData>) =
    TestService(
        localLoadNewest = localList.asLoadNewest(),
        localLoadOlder = localList.asLoadOlder(),
        localSave = localList.asSave(),
        remoteLoadNewest = remoteList.asLoadNewest(),
        remoteLoadNewer = remoteList.asLoadNewer(),
        remoteLoadOlder = remoteList.asLoadOlder(),
    )

class SetBasedServiceTest {
    private val limit = 2

    private fun context() = TestContext(limit)

    private fun <T> List<T>.contentFor(off: Int = 0, len: Int = 1) =
        drop(off * limit).take(len * limit).toSet()

    @Test
    fun `should initialize with local data`(): Unit = runBlocking {
        val localList = (0 until limit * 10).mapTo(mutableListOf(), ::data)

        val context = TestContext(limit)
        val service = localService(localList)

        Assert.assertEquals(localList.take(limit).toSet(), service.initialize(context))
        Assert.assertEquals(localList.take(limit).toSet(), context.data)
    }

    @Test
    fun `should load down from local source`(): Unit = runBlocking {
        val localList = (0 until limit * 10).mapTo(mutableListOf(), ::data)

        val context = TestContext(limit)
        val service = localService(localList)

        Assert.assertEquals(localList.contentFor(off = 0), service.initialize(context))
        Assert.assertEquals(localList.contentFor(len = 1), context.data)

        Assert.assertEquals(localList.contentFor(off = 1), service.loadOlder(context))
        Assert.assertEquals(localList.contentFor(len = 2), context.data)

        Assert.assertEquals(localList.contentFor(off = 2), service.loadOlder(context))
        Assert.assertEquals(localList.contentFor(len = 3), context.data)
    }

    @Test
    fun `should load down from local then remote sources`(): Unit = runBlocking {
        val localList = (0 until 2 * limit).mapTo(mutableListOf(), ::data)
        val remoteList = (0 until 4 * limit).mapTo(mutableListOf(), ::data)

        val context = context()
        val service = service(localList, remoteList)

        Assert.assertEquals(remoteList.contentFor(off = 0), service.initialize(context))
        Assert.assertEquals(remoteList.contentFor(len = 1), context.data)
        Assert.assertEquals(remoteList.take(limit * 2), localList)

        Assert.assertEquals(remoteList.contentFor(off = 1), service.loadOlder(context))
        Assert.assertEquals(remoteList.contentFor(len = 2), context.data)
        Assert.assertEquals(remoteList.take(limit * 2), localList)

        Assert.assertEquals(remoteList.contentFor(off = 2), service.loadOlder(context))
        Assert.assertEquals(remoteList.contentFor(len = 3), context.data)
        Assert.assertEquals(remoteList.take(limit * 3), localList)

        Assert.assertEquals(remoteList.contentFor(off = 3), service.loadOlder(context))
        Assert.assertEquals(remoteList.contentFor(len = 4), context.data)
        Assert.assertEquals(remoteList.take(limit * 4), localList)

        Assert.assertEquals(emptySet<TestData>(), service.loadOlder(context))
    }

    @Test
    fun `should initialize from remote`(): Unit = runBlocking {
        val localList = mutableListOf<TestData>()
        val remoteList = (0 until limit * 2).mapTo(mutableListOf(), ::data)

        var context = context()
        val service = service(localList, remoteList)

        Assert.assertEquals(emptySet<TestData>(), service.initialize(context))

        Assert.assertEquals(remoteList.contentFor(len = 1), service.loadNewer(context))
        Assert.assertEquals(remoteList.take(limit), localList)

        // Try using loadOlder
        localList.clear()
        context = context()

        Assert.assertEquals(emptySet<TestData>(), service.initialize(context))

        Assert.assertEquals(remoteList.contentFor(len = 1), service.loadOlder(context))
        Assert.assertEquals(remoteList.take(limit), localList)
    }

    @Test
    fun `should update local data from remote`(): Unit = runBlocking {
        val localList = (2 * limit until 4 * limit).mapTo(mutableListOf(), ::data)
        val remoteList = (0 until 4 * limit).mapTo(mutableListOf(), ::data)

        val context = context()
        val service = service(localList, remoteList)

        Assert.assertEquals(remoteList.contentFor(off = 2), service.initialize(context))
        Assert.assertEquals(remoteList.drop(2 * limit).toSet(), localList.toSet())

        Assert.assertEquals(remoteList.contentFor(off = 1), service.loadNewer(context))
        Assert.assertEquals(remoteList.drop(1 * limit).toSet(), localList.toSet())
        Assert.assertEquals(false, context.upToDate)

        Assert.assertEquals(remoteList.contentFor(off = 0), service.loadNewer(context))
        Assert.assertEquals(remoteList.toSet(), localList.toSet())
        Assert.assertEquals(false, context.upToDate)

        Assert.assertEquals(emptySet<TestData>(), service.loadNewer(context))
        Assert.assertEquals(true, context.upToDate)

    }
}

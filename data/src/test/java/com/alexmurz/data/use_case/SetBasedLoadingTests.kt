package com.alexmurz.data.use_case

import com.alexmurz.composetexter.libcore.service.set_service.AbstractSetBasedContext
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

private data class Data(
    val id: Int,
    val date: Long,
)

private class DataContext(limit: Int) : AbstractSetBasedContext<Data>(limit) {
    override fun getNewerReference(): Data? = data.maxByOrNull { it.date }
    override fun getOlderReference(): Data? = data.minByOrNull { it.date }
}

class SetBasedLoadingTests {
    private val limit = 3

    private fun context() = DataContext(limit)

    private fun data(id: Int) = Data(
        id = id,
        date = 1_000_000_000L - id.toLong(),
    )

    private fun IntRange.toDataset() = mapTo(mutableSetOf(), ::data)

    private fun loadNewestHandle(c: Collection<Data>): LoadNewestHandle<Data> = { ctx ->
        c.sortedByDescending { it.date }.take(ctx.limit).toSet()
    }

    private fun loadNewerHandle(c: Collection<Data>): LoadNewerHandle<Data> = { ctx, ref ->
        val above = ref.date
        c.sortedByDescending { it.date }.takeWhile { it.date > above }.takeLast(ctx.limit).toSet()
    }

    private fun loadOlderHandle(c: Collection<Data>): LoadNewerHandle<Data> = { ctx, ref ->
        val above = ref.date
        c.sortedByDescending { it.date }.dropWhile { it.date >= above }.take(ctx.limit).toSet()
    }

    private fun saveHandle(c: MutableCollection<Data>): SaveHandle<Data> = { _, data ->
        c.addAll(data)
    }

    private fun <T> unreachable(): T = throw Exception("Unreachable")

    @Test
    fun `should initialize small data`() = runBlocking {
        val context = context()

        val expect = setOf(data(0), data(1), data(2))

        val result = context.initialize(
            localLoadNewest = loadNewestHandle(expect)
        )

        Assert.assertEquals(expect, result)
        Assert.assertEquals(expect, context.data)
    }

    @Test
    fun `should initialize partially from big data`() = runBlocking {
        val context = context()

        val expect = setOf(data(0), data(1), data(2))
        val dataSet = (0..10).mapTo(mutableSetOf(), ::data)

        val result = context.initialize(
            localLoadNewest = loadNewestHandle(dataSet)
        )

        Assert.assertEquals(expect, result)
        Assert.assertEquals(expect, context.data)
    }

    @Test
    fun `should load newer data`() = runBlocking {
        val context = context()

        val dataSet = (0..10).toDataset()

        // Initialize with 3, 4, 5
        val localData = (3..5).toDataset()
        val result = context.initialize(
            localLoadNewest = loadNewestHandle(localData)
        )
        Assert.assertEquals(localData, result)
        Assert.assertEquals(localData, context.data)

        // Update to whole 0..5 by loading 0, 1, 2
        run {
            val expectedUpdate = (0..2).toDataset()
            val expectedLocalData = (0..5).toDataset()
            val receivedUpdate = context.loadNewer(
                localLoadNewest = { unreachable() },
                remoteLoadNewest = { unreachable() },
                remoteLoadNewer = loadNewerHandle(dataSet),
                localSave = saveHandle(localData),
            )
            Assert.assertEquals(expectedUpdate, receivedUpdate)
            Assert.assertEquals(expectedLocalData, localData)
            Assert.assertEquals(expectedLocalData, context.data)
            Assert.assertEquals(false, context.upToDate)
        }

        // No more data
        run {
            val expectedUpdate = emptySet<Data>()
            val expectedLocalData = (0..5).toDataset()
            val receivedUpdate = context.loadNewer(
                localLoadNewest = { unreachable() },
                remoteLoadNewest = { unreachable() },
                remoteLoadNewer = loadNewerHandle(dataSet),
                localSave = { _, _ -> unreachable() },
            )
            Assert.assertEquals(expectedUpdate, receivedUpdate)
            Assert.assertEquals(expectedLocalData, localData)
            Assert.assertEquals(expectedLocalData, context.data)
            Assert.assertEquals(true, context.upToDate)
        }
    }

    @Test
    fun `should initialize from remote because of empty local`() = runBlocking {
        val context = context()

        val local = mutableSetOf<Data>()
        val dataSet = (0..10).mapTo(mutableSetOf(), ::data)

        val result = context.initialize(
            localLoadNewest = loadNewestHandle(local)
        )
        Assert.assertEquals(emptySet<Data>(), result)

        val expectedUpdate = (0 until limit).toDataset()
        val updateResult = context.loadNewer(
            localLoadNewest = loadNewestHandle(local),
            remoteLoadNewest = loadNewestHandle(dataSet),
            remoteLoadNewer = loadNewerHandle(dataSet),
            localSave = saveHandle(local)
        )
        Assert.assertEquals(expectedUpdate, updateResult)
        Assert.assertEquals(expectedUpdate, local)
        Assert.assertEquals(expectedUpdate, context.data)
    }

    @Test
    fun `should load older data`() = runBlocking {
        val context = context()

        val dataSet = (0..5).toDataset()

        // Initialize with 1, 2, 3
        val localData = (0..2).toDataset()
        val result = context.initialize(
            localLoadNewest = loadNewestHandle(localData)
        )
        Assert.assertEquals(localData, result)
        Assert.assertEquals(localData, context.data)

        // Update to whole 0..5 by loading 3, 4, 5
        run {
            val expectedUpdate = (3..5).toDataset()
            val expectedLocalData = (0..5).toDataset()
            val receivedUpdate = context.loadOlder(
                localLoadNewest = { unreachable() },
                localLoadOlder = loadOlderHandle(localData),
                remoteLoadNewest = { unreachable() },
                remoteLoadOlder = loadOlderHandle(dataSet),
                localSave = saveHandle(localData),
            )
            Assert.assertEquals(expectedUpdate, receivedUpdate)
            Assert.assertEquals(expectedLocalData, localData)
            Assert.assertEquals(expectedLocalData, context.data)
        }

        // No more data, but we dont know yet
        run {
            val expectedUpdate = emptySet<Data>()
            val expectedLocalData = (0..5).toDataset()
            val receivedUpdate = context.loadOlder(
                localLoadNewest = { unreachable() },
                localLoadOlder = { _, _ -> unreachable() },
                remoteLoadNewest = { unreachable() },
                remoteLoadOlder = loadOlderHandle(dataSet),
                localSave = { _, _ -> unreachable() },
            )
            Assert.assertEquals(expectedUpdate, receivedUpdate)
            Assert.assertEquals(expectedLocalData, localData)
            Assert.assertEquals(expectedLocalData, context.data)
        }

        // No more data and we know it, no calls for remote
        run {
            val expectedUpdate = emptySet<Data>()
            val expectedLocalData = (0..5).toDataset()
            val receivedUpdate = context.loadOlder(
                localLoadNewest = { unreachable() },
                localLoadOlder = { _, _ -> unreachable() },
                remoteLoadNewest = { unreachable() },
                remoteLoadOlder = { _, _ -> unreachable() },
                localSave = { _, _ -> unreachable() },
            )
            Assert.assertEquals(expectedUpdate, receivedUpdate)
            Assert.assertEquals(expectedLocalData, localData)
            Assert.assertEquals(expectedLocalData, context.data)
        }
    }

}

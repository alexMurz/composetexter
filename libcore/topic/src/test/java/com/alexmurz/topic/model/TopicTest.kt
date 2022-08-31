package com.alexmurz.topic.model

import com.alexmurz.composetexter.libcore.CATime
import org.junit.Assert.*
import org.junit.Test

class TopicTest {
    @Test
    fun `should content equal two topics`() {
        val time = CATime.now()
        val a = Topic(
            id = 1,
            date = time,
            title = "123",
            message = "",
            attachments = emptyList()
        )
        val b = Topic(
            id = 1,
            date = time,
            title = "123",
            message = "",
            attachments = emptyList()
        )
        val c = Topic(
            id = 1,
            date = time,
            title = "123",
            message = "321",
            attachments = emptyList()
        )

        assertEquals(a, b)
        assert(a.isSameContent(b))

        assertEquals(a, c)
        assert(!a.isSameContent(c))
    }
}
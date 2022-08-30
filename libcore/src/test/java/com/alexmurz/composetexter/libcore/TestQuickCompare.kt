package com.alexmurz.composetexter.libcore

import com.alexmurz.composetexter.libcore.util.quickCompare
import org.junit.Assert
import org.junit.Test

data class Data(
    val id: Int,
    val data: Any?,
) {

    fun isSameContent(other: Any?) = quickCompare(other) {
        when {
            id != it.id -> false
            data != it.data -> false
            else -> true
        }
    }

    override fun equals(other: Any?) = quickCompare(other) {
        id == it.id
    }

    override fun hashCode(): Int = id.hashCode()
}

internal class TestQuickCompare {
    data class TestEntry(
        val a: Data,
        val b: Data,
        val shallow: Boolean,
        val content: Boolean
    )

    val testSet = listOf(
        TestEntry(
            a = Data(0, null),
            b = Data(0, null),
            shallow = true,
            content = true,
        ),
        TestEntry(
            a = Data(0, null),
            b = Data(0, Unit),
            shallow = true,
            content = false,
        ),
        TestEntry(
            a = Data(0, Unit),
            b = Data(0, null),
            shallow = true,
            content = false,
        ),
        TestEntry(
            a = Data(0, Unit),
            b = Data(0, Unit),
            shallow = true,
            content = true,
        ),

        TestEntry(
            a = Data(0, null),
            b = Data(1, null),
            shallow = false,
            content = false,
        ),
        TestEntry(
            a = Data(0, null),
            b = Data(1, Unit),
            shallow = false,
            content = false,
        ),
        TestEntry(
            a = Data(0, Unit),
            b = Data(1, null),
            shallow = false,
            content = false,
        ),
        TestEntry(
            a = Data(0, Unit),
            b = Data(1, Unit),
            shallow = false,
            content = false,
        ),

        TestEntry(
            a = Data(1, null),
            b = Data(0, null),
            shallow = false,
            content = false,
        ),
        TestEntry(
            a = Data(1, null),
            b = Data(0, Unit),
            shallow = false,
            content = false,
        ),
        TestEntry(
            a = Data(1, Unit),
            b = Data(0, null),
            shallow = false,
            content = false,
        ),
        TestEntry(
            a = Data(1, Unit),
            b = Data(0, Unit),
            shallow = false,
            content = false,
        ),

        TestEntry(
            a = Data(1, null),
            b = Data(1, null),
            shallow = true,
            content = true,
        ),
        TestEntry(
            a = Data(1, null),
            b = Data(1, Unit),
            shallow = true,
            content = false,
        ),
        TestEntry(
            a = Data(1, Unit),
            b = Data(1, null),
            shallow = true,
            content = false,
        ),
        TestEntry(
            a = Data(1, Unit),
            b = Data(1, Unit),
            shallow = true,
            content = true,
        ),
    )

    @Test
    fun `should shallow equal`() {
        testSet.forEachIndexed { index, testEntry ->
            if (testEntry.shallow) Assert.assertEquals(
                "Shallow test #$index failed. Expected equals\n${testEntry.a}\n${testEntry.b}",
                testEntry.a,
                testEntry.b,
            ) else Assert.assertNotEquals(
                "Shallow test #$index failed. Expected not equals\n${testEntry.a}\n${testEntry.b}",
                testEntry.a,
                testEntry.b,
            )
        }
    }

    @Test
    fun `should fail`() {
        assert(true)
    }

    @Test
    fun `should content equal`() {
        testSet.forEachIndexed { index, testEntry ->
            if (testEntry.content) {
                assert(testEntry.a.isSameContent(testEntry.b)) {
                    "Content test #$index failed. Expected equals\n${testEntry.a}\n${testEntry.b}"
                }
            } else {
                assert(!testEntry.a.isSameContent(testEntry.b)) {
                    "Content test #$index failed. Expected not equals\n${testEntry.a}\n${testEntry.b}"
                }
            }
        }
    }
}


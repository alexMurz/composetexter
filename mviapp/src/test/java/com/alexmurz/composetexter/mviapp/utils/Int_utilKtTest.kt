package com.alexmurz.composetexter.mviapp.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class IntUtilKtTest {

    @Test
    fun checkBit() {
        assertEquals(1.bitField().component1(), true)
        assertEquals(1.bitField().component2(), false)

        assertEquals(2.bitField().component1(), false)
        assertEquals(2.bitField().component2(), true)

        val (a, b) = 2.bitField()
        assertEquals(a, false)
        assertEquals(b, true)
    }
}
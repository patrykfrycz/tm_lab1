package com.example.fryczl4

import org.junit.Assert.assertEquals
import org.junit.Test

class ComplexMathTest {

    @Test
    fun testAddition() {
        val aRe = 2.0
        val aIm = 3.0
        val bRe = 1.0
        val bIm = -4.0

        val resRe = aRe + bRe
        val resIm = aIm + bIm

        assertEquals(3.0, resRe, 0.0001)
        assertEquals(-1.0, resIm, 0.0001)
    }

    @Test
    fun testMultiplication() {
        // (2 + 3i)(1 - 4i) = 2 - 8i + 3i - 12i^2 = 2 - 5i + 12 = 14 - 5i
        val aRe = 2.0
        val aIm = 3.0
        val bRe = 1.0
        val bIm = -4.0

        val resRe = aRe * bRe - aIm * bIm
        val resIm = aRe * bIm + aIm * bRe

        assertEquals(14.0, resRe, 0.0001)
        assertEquals(-5.0, resIm, 0.0001)
    }
}

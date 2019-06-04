package io.chengguo.streaming

import org.junit.Test

class HexConvertTest {
    @Test
    fun byteShiftTest() {
        val i = 127
        println(i.toByte())
        val b: Byte = -5
        println(Integer.toBinaryString(b.toInt()))
    }
}
@file:Suppress("NOTHING_TO_INLINE")

package com.alexmurz.composetexter.mviapp.utils

@JvmInline
value class BitField(
    val int: Int
) {
    constructor(
        b0: Boolean = false,
        b1: Boolean = false,
        b2: Boolean = false,
        b3: Boolean = false,
        b4: Boolean = false,
        b5: Boolean = false,
        b6: Boolean = false,
        b7: Boolean = false,
        b8: Boolean = false,
        b9: Boolean = false,
    ) : this(
        b0.toIntShl(0) +
            b1.toIntShl(1) +
            b2.toIntShl(2) +
            b3.toIntShl(3) +
            b4.toIntShl(4) +
            b5.toIntShl(5) +
            b6.toIntShl(6) +
            b7.toIntShl(7) +
            b8.toIntShl(8) +
            b9.toIntShl(9)
    )

    operator fun component1() = int.checkBit(0)
    operator fun component2() = int.checkBit(1)
    operator fun component3() = int.checkBit(2)
    operator fun component4() = int.checkBit(3)
    operator fun component5() = int.checkBit(4)
    operator fun component6() = int.checkBit(5)
    operator fun component7() = int.checkBit(6)
    operator fun component8() = int.checkBit(7)
    operator fun component9() = int.checkBit(8)
    operator fun component10() = int.checkBit(9)
}

inline fun Int.bitField(): BitField = BitField(this)

inline fun Int.checkBit(bit: Int): Boolean = ((this shr bit) and 1) > 0

inline fun Boolean.toIntShl(shift: Int): Int {
    val v = if (this) 1 else 0
    return v shl shift
}

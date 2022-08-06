package com.alexmurz.composetexter.libcore.util

inline fun <reified T> T?.quickCompare(b: Any?, transform: T.(other: T) -> Boolean): Boolean {
    val a = this
    if (a === b) return true
    if (a === null || b === null) return false
    if (T::class.java !== b.javaClass) return false
    b as T

    return transform(a, b)
}
package com.alexmurz.composetexter.libcore.util

/**
 * Apply transformer function to this if flag is true
 * otherwise returns this
 */
fun <T> T.remapIf(flag: Boolean, transformer: (T) -> T): T =
    if (flag) transformer(this) else this

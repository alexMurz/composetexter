package com.alexmurz.composetexter.libcore.util

inline fun <T> Boolean.then(action: () -> T): T? {
    return if (this) action()
    else null
}

inline fun <T> Boolean.otherwise(action: () -> T): T? {
    return (!this).then(action)
}

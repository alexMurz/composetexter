package com.alexmurz.composetexter.util

import android.util.Log

interface Loggable {
    fun log(message: String)
}

class AndroidLoggable(
    private val tag: String,
    private val enabled: Boolean = true,
) : Loggable {

    override fun log(message: String) {
        if (enabled) Log.i(tag, message)
    }
}
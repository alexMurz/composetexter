package com.alexmurz.composetexter.libcore

import org.joda.time.DateTime

/**
 * Core-App Time
 */
class CATime private constructor(millis: Long): Comparable<CATime> {
    private val impl: DateTime = DateTime(millis)

    val timestamp: Long = impl.millis

    val year: Int
        get() = impl.year

    val monthOfYear: Int
        get() = impl.monthOfYear

    val weekOfYear: Int
        get() = impl.weekOfWeekyear

    val dayOfMonth: Int
        get() = impl.dayOfMonth

    val dayOfWeek: Int
        get() = impl.dayOfWeek

    val dayOfYear: Int
        get() = impl.dayOfYear

    val hourOfDay: Int
        get() = impl.hourOfDay

    val minuteOfDay: Int
        get() = impl.minuteOfDay

    val minuteOfHour: Int
        get() = impl.minuteOfHour

    val secondOfMinute: Int
        get() = impl.secondOfMinute

    val secondOfDay: Int
        get() = impl.secondOfDay

    override fun compareTo(other: CATime): Int = impl.compareTo(other.impl)

    override fun equals(other: Any?) = when (other) {
        is CATime -> impl == other.impl
        else -> false
    }

    override fun hashCode() = impl.hashCode()

    override fun toString() = "Time($impl)"

    companion object {
        const val SECOND_MILLIS = 1000L
        const val MINUTE_MILLIS = SECOND_MILLIS * 60
        const val HOUR_MILLIS = MINUTE_MILLIS * 60
        const val DAY_MILLIS = HOUR_MILLIS * 24

        fun now(): CATime = CATime(System.currentTimeMillis())

        fun of(millis: Long): CATime = CATime(millis)
    }
}



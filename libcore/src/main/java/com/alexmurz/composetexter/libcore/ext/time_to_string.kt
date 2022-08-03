package com.alexmurz.composetexter.libcore.ext

import com.alexmurz.composetexter.libcore.CATime

interface TimeStringFormatter {
    fun today(hour: Int, minute: Int): String

    fun yesterday(hour: Int, minute: Int): String

    fun inLast7Days(hour: Int, minute: Int, dayOfWeek: Int): String

    fun thisYear(hour: Int, minute: Int, dayOfMonth: Int, monthOfYear: Int): String

    fun fullFormat(hour: Int, minute: Int, dayOfMonth: Int, monthOfYear: Int, year: Int): String

    companion object : TimeStringFormatter {

        private fun dayOfWeek(dayOfWeek: Int): String = when (dayOfWeek) {
            1 -> "monday"
            2 -> "tuesday"
            3 -> "wednesday"
            4 -> "thursday"
            5 -> "friday"
            6 -> "saturday"
            else -> "sunday"
        }

        private fun monthOfYear(monthOfYear: Int): String = when (monthOfYear) {
            1 -> "january"
            2 -> "february"
            3 -> "march"
            4 -> "april"
            5 -> "may"
            6 -> "jun"
            7 -> "july"
            8 -> "august"
            9 -> "september"
            10 -> "october"
            11 -> "november"
            else -> "december"
        }


        override fun today(
            hour: Int, minute: Int
        ): String {
            return String.format("%d:%02d", hour, minute)
        }

        override fun yesterday(
            hour: Int, minute: Int
        ): String {
            return String.format("yesterday, %d:%02d", hour, minute)
        }

        override fun inLast7Days(
            hour: Int,
            minute: Int,
            dayOfWeek: Int
        ): String {
            val weekdayString = dayOfWeek(dayOfWeek)
            return String.format("%s, %d:%02d", weekdayString, hour, minute)
        }

        override fun thisYear(
            hour: Int,
            minute: Int,
            dayOfMonth: Int,
            monthOfYear: Int
        ): String {
            val month = monthOfYear(monthOfYear)
            return String.format("%s %s, %d:%02d", month, dayOfMonth, hour, minute)
        }

        override fun fullFormat(
            hour: Int,
            minute: Int,
            dayOfMonth: Int,
            monthOfYear: Int,
            year: Int
        ): String {
            val month = monthOfYear(monthOfYear)
            return String.format("%d %s %s, %d:%02d", year, month, dayOfMonth, hour, minute)
        }
    }
}

fun CATime.timeStringSince(
    formatter: TimeStringFormatter = TimeStringFormatter,
    now: CATime = CATime.now(),
): String {
    val y1 = year
    val m1 = monthOfYear
    val dy1 = dayOfYear
    val d1 = dayOfMonth

    val y2 = now.year
    val m2 = now.monthOfYear
    val dy2 = now.dayOfYear
    val d2 = now.dayOfMonth

    return when {
        y1 == y2 && m1 == m2 && d1 == d2 -> formatter.today(hourOfDay, minuteOfHour)
        y1 == y2 && dy2 - dy1 == 1 -> formatter.yesterday(hourOfDay, minuteOfHour)
        y1 == y2 && dy2 - dy1 < 7 -> formatter.inLast7Days(hourOfDay, minuteOfHour, dayOfWeek)
        y1 == y2 -> formatter.thisYear(hourOfDay, minuteOfHour, dayOfMonth, monthOfYear)
        else -> formatter.fullFormat(
            hourOfDay,
            minuteOfHour,
            dayOfMonth,
            monthOfYear,
            year
        )
    }
}

package com.example.yebon.one_todo.utils

import java.util.*

class DateUtil {
    companion object {
        fun getDateStr(year: Int, month: Int, day: Int): String {
            return "${year}.${month}.${day}"
        }
    }
}

fun Calendar.getNowYear(): Int {
    return get(Calendar.YEAR)
}

fun Calendar.getNowMonth(): Int {
    return get(Calendar.MONTH) + 1
}

fun Calendar.getNowDay(): Int {
    return get(Calendar.DAY_OF_MONTH)
}
package com.example.yebon.one_todo.utils

import java.util.*

fun Calendar.getNowYear(): Int {
    return get(Calendar.YEAR)
}

fun Calendar.getNowMonth(): Int {
    return get(Calendar.MONTH) + 1
}

fun Calendar.getNowDay(): Int {
    return get(Calendar.DAY_OF_MONTH)
}
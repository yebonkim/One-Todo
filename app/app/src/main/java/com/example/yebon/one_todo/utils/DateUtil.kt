package com.example.yebon.one_todo.utils

import java.util.*

fun Calendar.getNowYear(): Int {
    return get(Calendar.YEAR)
}

fun Calendar.getNowMonth(): Int {
    return get(Calendar.MONTH) + 1
}
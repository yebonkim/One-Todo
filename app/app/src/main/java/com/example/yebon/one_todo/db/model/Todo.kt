package com.example.yebon.one_todo.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yebon.one_todo.utils.getNowDay
import com.example.yebon.one_todo.utils.getNowMonth
import com.example.yebon.one_todo.utils.getNowYear
import java.util.*

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var contents: String,
    val year: Int,
    val month: Int,
    val day: Int,
    var isDone: Boolean
) {
    constructor(year: Int, month: Int, day: Int) : this(0, "", year, month, day, false)

    companion object {
        fun makeTodayTodo(calendar: Calendar): Todo {
            return Todo(calendar.getNowYear(), calendar.getNowMonth(), calendar.getNowDay())
        }
    }

    fun isTodayTodo(calendar: Calendar): Boolean {
        val todayYear = calendar.getNowYear()
        val todayMonth = calendar.getNowMonth()
        val todayDay = calendar.getNowDay()

        return this.year == todayYear && this.month == todayMonth && this.day == todayDay
    }
}
package com.example.yebon.one_todo

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.yebon.one_todo.adapter.TodoAdapter
import com.example.yebon.one_todo.db.AppDatabase
import com.example.yebon.one_todo.db.TodoDAO
import com.example.yebon.one_todo.db.model.Todo
import com.example.yebon.one_todo.utils.getNowDay
import com.example.yebon.one_todo.utils.getNowMonth
import com.example.yebon.one_todo.utils.getNowYear
import com.example.yebon.one_todo.view.YearMonthDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

interface MainContract {

    interface View {
        fun getAppContext(): Context
    }

    interface Presenter {
        fun loadMinYear()
        fun loadTodos(year: Int, month: Int, setTodoOnView: (List<Todo>) -> Unit)
        fun addTodo(content: String, onTodoAdded: (Todo) -> Unit)
        fun getNowYear(): Int
        fun getNowMonth(): Int
        fun getNowDay(): Int
        fun getMinYear(): Int
        fun makeTodayTodo(): Todo
        fun removeTodayTodo(todos: MutableList<Todo>): MutableList<Todo>
        fun getTodayTodo(todos: List<Todo>): Todo?
        fun isThisMonth(year: Int, month: Int): Boolean
        fun getTodoDAO(): TodoDAO
    }
}
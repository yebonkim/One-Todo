package com.example.yebon.one_todo

import androidx.room.Room
import com.example.yebon.one_todo.db.AppDatabase
import com.example.yebon.one_todo.db.model.Todo
import com.example.yebon.one_todo.utils.getNowDay
import com.example.yebon.one_todo.utils.getNowMonth
import com.example.yebon.one_todo.utils.getNowYear
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainPresenter(val mView: MainContract.View) : MainContract.Presenter {

    private var minYear = AppDatabase.INVALID_YEAR

    private val db by lazy {
        Room.databaseBuilder(mView.getAppContext(), AppDatabase::class.java,
            AppDatabase.DATABASE_NAME).build()
    }

    private val calendar by lazy {
        Calendar.getInstance()
    }

    override fun loadMinYear() {
        db.todoDao()
            .getMinYear()
            .subscribeOn(Schedulers.io())
            .subscribe({
                minYear = it
            }, {
                minYear = calendar.getNowYear()
            })
    }

    override fun loadTodos(year: Int, month: Int, setTodoOnView: (List<Todo>) -> Unit) {
        db.todoDao()
            .getTodos(year, month)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(setTodoOnView, { it.printStackTrace() })
    }

    override fun addTodo(content: String, onTodoAdded: (Todo) -> Unit) {
        val newTodo = Todo(0, content, calendar.getNowYear(), calendar.getNowMonth(),
            calendar.getNowDay(), false)
        db.todoDao().addNewTodo(newTodo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onTodoAdded(newTodo)
            }, {
                it.printStackTrace()
            })
    }

    override fun getNowYear() = calendar.getNowYear()
    override fun getNowMonth() = calendar.getNowMonth()
    override fun getNowDay() = calendar.getNowDay()

    override fun getMinYear() = if (minYear != AppDatabase.INVALID_YEAR) minYear else calendar.getNowYear()

    override fun makeTodayTodo() = Todo.makeTodayTodo(calendar)

    override fun removeTodayTodo(todos: MutableList<Todo>): MutableList<Todo> {
        val todayTodo = getTodayTodo(todos)

        if (todayTodo != null) {
            todos.removeAt(0)
        }

        return todos
    }

    override fun getTodayTodo(todos: List<Todo>): Todo? {
        if (todos.isEmpty()) {
            return null
        }

        return if (todos[0].isTodayTodo(calendar)) todos[0] else null
    }

    override fun isThisMonth(year: Int, month: Int): Boolean {
        return year == getNowYear() && month == getNowMonth()
    }
}
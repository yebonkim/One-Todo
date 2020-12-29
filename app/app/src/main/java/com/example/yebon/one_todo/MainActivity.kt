package com.example.yebon.one_todo

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.yebon.one_todo.adapter.TodoAdapter
import com.example.yebon.one_todo.db.AppDatabase
import com.example.yebon.one_todo.db.model.Todo
import com.example.yebon.one_todo.utils.getNowDay
import com.example.yebon.one_todo.utils.getNowMonth
import com.example.yebon.one_todo.utils.getNowYear
import com.example.yebon.one_todo.view.YearMonthDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val calendar by lazy {
        Calendar.getInstance()
    }

    private val db by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java,
            AppDatabase.DATABASE_NAME).build()
    }

    private val onDismissListener = {selectedYear: Int, selectedMonth: Int ->
        year.text = selectedYear.toString()
        month.text = selectedMonth.toString()
    }

    private var minYear = AppDatabase.INVALID_YEAR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        year.text = calendar.getNowYear().toString()
        month.text = calendar.getNowMonth().toString()
        date_container.setOnClickListener(this)
        confirm.setOnClickListener(this)

        db.todoDao()
            .getMinYear()
            .subscribeOn(Schedulers.io())
            .subscribe({
                minYear = it
            }, {
                minYear = calendar.getNowYear()
            })

        db.todoDao()
            .getTodos(calendar.getNowYear(), calendar.getNowMonth())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val latestTodo = getLatestTodo(it)

                if (latestTodo == null) {
                    showAddingTodayTodoViews(Todo.makeTodayTodo(calendar))
                } else {
                    showTodayTodoViews(latestTodo)
                }

                setRecyclerView(it.toMutableList())
            }, {
                it.printStackTrace()
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.date_container -> showDatePicker()
            R.id.confirm -> {
                val content = today_todo_edit.text.toString()

                if (!TextUtils.isEmpty(content)) {
                    db.todoDao().addNewTodo(
                        Todo(
                            0,
                            content,
                            calendar.getNowYear(),
                            calendar.getNowMonth(),
                            calendar.getNowDay(),
                            false
                        )
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            Toast.makeText(this, "inserted!", Toast.LENGTH_SHORT).show()
                        }, {
                            it.printStackTrace()
                        })
                } else {
                    Toast.makeText(this, R.string.input_todo, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getSelectedYear() = Integer.parseInt(year.text.toString())
    private fun getSelectedMonth() = Integer.parseInt(month.text.toString())

    private fun showDatePicker() {
        YearMonthDialog(this, getSelectedYear(), getSelectedMonth(), minYear, onDismissListener).show()
    }

    private fun getLatestTodo(todos: List<Todo>): Todo? {
        if (todos.isEmpty()) {
            return null
        }
        return todos[0];
    }

    private fun showAddingTodayTodoViews(todo: Todo) {
        today_todo_edit.visibility = View.VISIBLE
        today_todo.visibility = View.GONE
        confirm.visibility = View.VISIBLE

        today_todo_edit.setText(todo.contents)
    }

    private fun showTodayTodoViews(todo: Todo) {
        today_todo_edit.visibility = View.GONE
        today_todo.visibility = View.VISIBLE
        confirm.visibility = View.GONE

        today_todo.text = todo.contents
    }

    private fun setRecyclerView(todos: MutableList<Todo>) {
        val todoIterator = todos.iterator()

        while (todoIterator.hasNext()) {
            if (todoIterator.next().isTodayTodo(calendar)) {
                todoIterator.remove()
            }
        }

        list.layoutManager = LinearLayoutManager(applicationContext)
        list.adapter = TodoAdapter(todos)
    }
}
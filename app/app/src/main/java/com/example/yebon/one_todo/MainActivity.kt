package com.example.yebon.one_todo

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.yebon.one_todo.adapter.TodoAdapter
import com.example.yebon.one_todo.db.AppDatabase
import com.example.yebon.one_todo.db.model.Todo
import com.example.yebon.one_todo.utils.getNowMonth
import com.example.yebon.one_todo.utils.getNowYear
import com.example.yebon.one_todo.view.YearMonthDialog
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

        AsyncTask.execute{
            minYear = db.todoDao().getMinYear()
            list.run {
                val todos = db.todoDao().getTodos(calendar.getNowYear(), calendar.getNowMonth())
                adapter = TodoAdapter(addTodayTodo(todos.toMutableList()), db.todoDao())
                layoutManager = LinearLayoutManager(context)
            }
        }
        year.text = calendar.getNowYear().toString()
        month.text = calendar.getNowMonth().toString()
        date_container.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.date_container -> showDatePicker()
        }
    }

    private fun showDatePicker() {
        YearMonthDialog(this, getSelectedYear(), getSelectedMonth(), minYear, onDismissListener).show()
    }

    private fun getSelectedYear() = Integer.parseInt(year.text.toString())
    private fun getSelectedMonth() = Integer.parseInt(month.text.toString())

    private fun addTodayTodo(list: MutableList<Todo>): List<Todo> {
        val year = calendar.getNowYear()
        val month = calendar.getNowMonth()
        if (list.isEmpty() || list.get(0).isTodayTodo(year, month)) {
            list.add(Todo(year, month))
        }

        return list
    }
}
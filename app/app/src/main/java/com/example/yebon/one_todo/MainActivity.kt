package com.example.yebon.one_todo

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yebon.one_todo.adapter.TodoAdapter
import com.example.yebon.one_todo.db.model.Todo
import com.example.yebon.one_todo.view.YearMonthDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, MainContract.View {

    private val mPresenter by lazy {
        MainPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        year.text = mPresenter.getNowYear().toString()
        month.text = mPresenter.getNowMonth().toString()
        date_container.setOnClickListener(this)
        confirm.setOnClickListener(this)

        mPresenter.loadMinYear()
        mPresenter.loadTodos(mPresenter.getNowYear(), mPresenter.getNowMonth(), setTodosOnView)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.date_container -> showDatePicker()
            R.id.confirm -> {
                val content = today_todo_edit.text.toString()

                if (!TextUtils.isEmpty(content)) {
                    mPresenter.addTodo(content, setInsertedTodoOnView)
                } else {
                    Toast.makeText(this, R.string.input_todo, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getAppContext(): Context {
        return applicationContext
    }

    private val setTodosOnView = fun (todos: List<Todo>) {
        val todayTodo = mPresenter.getTodayTodo(todos)
        val todayRemovedList = mPresenter.removeTodayTodo(todos.toMutableList())

        updateTodayTodo(todayTodo)

        if (todayRemovedList.isEmpty()) {
            showEmptyContainer()
        } else {
            showList()
            setRecyclerView(todayRemovedList)
        }

    }

    private val setInsertedTodoOnView = fun (todo: Todo) {
        Toast.makeText(this, R.string.todo_added, Toast.LENGTH_SHORT).show()
        updateTodayTodo(todo)
    }

    private val onDismissListener = {selectedYear: Int, selectedMonth: Int ->
        year.text = selectedYear.toString()
        month.text = selectedMonth.toString()
        mPresenter.loadTodos(selectedYear, selectedMonth, setTodosOnView)
    }

    private fun updateTodayTodo(todo: Todo?) {
        if (mPresenter.isThisMonth(getSelectedYear(), getSelectedMonth())) {
            if (todo == null) {
                showAddingTodayTodoViews(mPresenter.makeTodayTodo())
            } else {
                showWrittenTodayTodo(todo)
            }
        } else {
            hideTodayTodoContainer()
        }
    }

    private fun showDatePicker() {
        YearMonthDialog(this, getSelectedYear(), getSelectedMonth(), mPresenter.getMinYear(),
            mPresenter.getNowYear(), onDismissListener).show()
    }

    private fun showAddingTodayTodoViews(todo: Todo) {
        showTodayTodoContainer()
        today_todo_edit.visibility = View.VISIBLE
        today_todo.visibility = View.GONE
        confirm.visibility = View.VISIBLE

        today_todo_edit.setText(todo.contents)
    }

    private fun showWrittenTodayTodo(todo: Todo) {
        showTodayTodoContainer()
        today_todo_edit.visibility = View.GONE
        today_todo.visibility = View.VISIBLE
        confirm.visibility = View.GONE

        today_todo.text = todo.contents
    }

    private fun hideTodayTodoContainer() {
        today.visibility = View.GONE
        today_todo_container.visibility = View.GONE
    }

    private fun showTodayTodoContainer() {
        today.visibility = View.VISIBLE
        today_todo_container.visibility = View.VISIBLE
    }

    private fun setRecyclerView(todos: MutableList<Todo>) {
        list.layoutManager = LinearLayoutManager(applicationContext)
        list.adapter = TodoAdapter(todos)
    }

    private fun showList() {
        list.visibility = View.VISIBLE
        empty_container.visibility = View.GONE
    }

    private fun showEmptyContainer() {
        list.visibility = View.GONE
        empty_container.visibility = View.VISIBLE
    }

    private fun getSelectedYear() = Integer.parseInt(year.text.toString())
    private fun getSelectedMonth() = Integer.parseInt(month.text.toString())
}
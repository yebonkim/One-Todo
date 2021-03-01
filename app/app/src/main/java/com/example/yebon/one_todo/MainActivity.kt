package com.example.yebon.one_todo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yebon.one_todo.adapter.TodoAdapter
import com.example.yebon.one_todo.db.model.Todo
import com.example.yebon.one_todo.view.AddingDialog
import com.example.yebon.one_todo.view.AskLoginDialog
import com.example.yebon.one_todo.view.KeyboardDetectingEditText
import com.example.yebon.one_todo.view.YearMonthDialog
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener, MainContract.View {

    companion object {
        const val IS_FIRST_LAUNCH = "isFirstLaunch"
        const val AUTH_UI_REQUEST_CODE = 2021
    }

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
        new_todo_btn.setOnClickListener(this)
        delete.setOnClickListener(this)
        today_todo_edit.setOnTouchListener(this)
        
        today_todo_edit.setKeyBoardHideListener(object : KeyboardDetectingEditText.OnKeyboardHideListener {
            override fun onHideKeyboard() {
                val content = today_todo_edit.text.toString()

                if (!TextUtils.isEmpty(content)) {
                    when (mPresenter.getConfirmBtnMode()) {
                        ConfirmBtnModes.INSERT_MODE -> {
                            addTodayTodo(content)
                        }
                        ConfirmBtnModes.CONTENT_UPDATE_MODE -> {
                            updateTodayTodoContent(content)
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, R.string.input_todo, Toast.LENGTH_SHORT).show()
                    loadTodosAndUpdateView(getSelectedYear(), getSelectedMonth())
                }
            }
        })

        mPresenter.loadMinYear()
        mPresenter.loadMaxYear()

        if (isFirstLaunch()) {
            AskLoginDialog(this) {
                requestAuthUI()
            }.show()
            setIsFirstLaunch(false)
        } else {
            loadTodosAndUpdateView(mPresenter.getNowYear(), mPresenter.getNowMonth())
        }
    }

    override fun onResume() {
        super.onResume()
        loadTodosAndUpdateView(getSelectedYear(), getSelectedMonth())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.date_container -> showDatePicker()
            R.id.confirm -> {
                val content = today_todo_edit.text.toString()

                if (!TextUtils.isEmpty(content)) {
                    when (mPresenter.getConfirmBtnMode()) {
                        ConfirmBtnModes.NONE -> {
                            toastError()
                        }
                        ConfirmBtnModes.INSERT_MODE -> {
                            addTodayTodo(content)
                        }
                        ConfirmBtnModes.CONTENT_UPDATE_MODE -> {
                            updateTodayTodoContent(content)
                        }
                        ConfirmBtnModes.DONE_UPDATE_MODE -> {
                            val todo = mPresenter.getTodayTodo()

                            if (todo == null) {
                                toastError()
                            } else {
                                todo.isDone = !todo.isDone
                                mPresenter.updateTodo(todo) {
                                    Toast.makeText(this, R.string.todo_updated, Toast.LENGTH_SHORT).show()
                                    hideKeyboard(today_todo_edit)
                                    loadTodosAndUpdateView(mPresenter.getNowYear(), mPresenter.getNowMonth())
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.input_todo, Toast.LENGTH_SHORT).show()
                }
            }
            R.id.new_todo_btn -> {
                AddingDialog(this, mPresenter.getTodoDAO(), mPresenter.getNowYear(),
                    mPresenter.getNowMonth(), mPresenter.getNowDay(), fun(year: Int, month: Int) {
                        mPresenter.loadMinYear()
                        mPresenter.loadMaxYear()
                        onDismissListener(year, month)
                    }).show()
            }
            R.id.delete -> {
                val todayTodo = mPresenter.getTodayTodo()

                if (todayTodo == null) {
                    toastError()
                } else {
                    mPresenter.deleteTodo(todayTodo) {
                        Toast.makeText(this, R.string.todo_deleted, Toast.LENGTH_SHORT).show()
                        hideKeyboard(today_todo_edit)
                        loadTodosAndUpdateView(getSelectedYear(), getSelectedMonth())
                    }
                }
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v?.id) {
            R.id.today_todo_edit -> {
                if (mPresenter.getConfirmBtnMode() == ConfirmBtnModes.DONE_UPDATE_MODE) {
                    mPresenter.setConfirmBtnMode(ConfirmBtnModes.CONTENT_UPDATE_MODE)
                    showEditingTodayTodo()
                }
                return false
            }
        }

        return false
    }

    private fun getSelectedYear() = Integer.parseInt(year.text.toString())
    private fun getSelectedMonth() = Integer.parseInt(month.text.toString())

    override fun getAppContext(): Context {
        return applicationContext
    }

    override fun toastError() {
        Toast.makeText(applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
    }

    private fun showDatePicker() {
        YearMonthDialog(this, getSelectedYear(), getSelectedMonth(), mPresenter.getMinYear(),
            mPresenter.getMaxYear(), onDismissListener).show()
    }

    private val onDismissListener = {selectedYear: Int, selectedMonth: Int ->
        year.text = selectedYear.toString()
        month.text = selectedMonth.toString()
        loadTodosAndUpdateView(selectedYear, selectedMonth)
    }

    private fun loadTodosAndUpdateView(year: Int, month: Int) {
        mPresenter.loadTodos(year, month, setTodosOnView)
    }

    private val setTodosOnView = fun (todayRemovedTodos: List<Todo>, todayTodo: Todo?) {
        if (mPresenter.isThisMonth(getSelectedYear(), getSelectedMonth())) {
            setTodayTodoToView(todayTodo)
        } else {
            hideTodayTodoContainer()
        }

        if (todayRemovedTodos.isEmpty()) {
            list.visibility = View.GONE
            empty_container.visibility = View.VISIBLE
        } else {
            list.visibility = View.VISIBLE
            empty_container.visibility = View.GONE
            setRecyclerView(todayRemovedTodos.toMutableList())
        }
    }

    private fun setTodayTodoToView(todayTodo: Todo?) {
        if (todayTodo == null) {
            mPresenter.setConfirmBtnMode(ConfirmBtnModes.INSERT_MODE)
            showAddTodayTodoViews()
        } else {
            mPresenter.setConfirmBtnMode(ConfirmBtnModes.DONE_UPDATE_MODE)
            showWrittenTodayTodo(todayTodo)
        }
    }

    private fun showAddTodayTodoViews() {
        showTodayTodoContainer()
        today_todo_edit.isCursorVisible = true
        today_todo_edit.setTextColor(getColor(R.color.black))
        delete.visibility = View.GONE
        confirm.text = getString(R.string.add_todo)
        confirm.background = resources.getDrawable(R.drawable.rounded_primary_color_background)

        today_todo_edit.setText("")

    }

    private fun showWrittenTodayTodo(todo: Todo) {
        showTodayTodoContainer()
        delete.visibility = View.VISIBLE
        today_todo_edit.isCursorVisible = false
        today_todo_edit.clearFocus()
        confirm.text = if (todo.isDone) getString(R.string.do_more) else getString(R.string.done)
        confirm.setBackground(
            if (todo.isDone) {
                resources.getDrawable(R.drawable.rounded_gray_background)
            } else {
                resources.getDrawable(R.drawable.rounded_primary_color_background)
            }
        )

        today_todo_edit.setText(todo.contents)
    }

    private fun showEditingTodayTodo() {
        today_todo_edit.isCursorVisible = true
        today_todo_edit.setTextColor(getColor(R.color.black))
        confirm.text = getString(R.string.update_todo)
        confirm.background = resources.getDrawable(R.drawable.rounded_primary_color_background)
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
        list.adapter = TodoAdapter(todos, {loadTodosAndUpdateView(getSelectedYear(), getSelectedMonth())})
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateTodayTodoContent(content: String) {
        val todo = mPresenter.getTodayTodo()

        if (todo == null) {
            toastError()
        } else {
            todo.contents = content
            mPresenter.updateTodo(todo) {
                Toast.makeText(this, R.string.todo_updated, Toast.LENGTH_SHORT).show()
                hideKeyboard(today_todo_edit)
                loadTodosAndUpdateView(mPresenter.getNowYear(), mPresenter.getNowMonth())
            }
        }
    }

    private fun addTodayTodo(content: String) {
        mPresenter.addTodo(content) {
            Toast.makeText(this, R.string.todo_added, Toast.LENGTH_SHORT).show()
            loadTodosAndUpdateView(mPresenter.getNowYear(), mPresenter.getNowMonth())
        }
    }

    private fun requestAuthUI() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(mPresenter.getAuthProvider())
                .build(), AUTH_UI_REQUEST_CODE)
    }

    private fun isFirstLaunch(): Boolean {
        return getPreferences(Context.MODE_PRIVATE).getBoolean(IS_FIRST_LAUNCH, true)
    }

    private fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        getPreferences(Context.MODE_PRIVATE).edit().putBoolean(IS_FIRST_LAUNCH, isFirstLaunch).apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AUTH_UI_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
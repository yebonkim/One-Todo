package com.example.yebon.one_todo

import androidx.room.Room
import com.example.yebon.one_todo.db.AppDatabase
import com.example.yebon.one_todo.db.TodoDAO
import com.example.yebon.one_todo.db.model.Todo
import com.example.yebon.one_todo.utils.getNowDay
import com.example.yebon.one_todo.utils.getNowMonth
import com.example.yebon.one_todo.utils.getNowYear
import com.firebase.ui.auth.AuthUI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class MainPresenter(val mView: MainContract.View) : MainContract.Presenter {

    private var minYear = AppDatabase.INVALID_YEAR
    private var maxYear = AppDatabase.INVALID_YEAR
    @ConfirmBtnModes.ConfirmBtnMode
    private var mConfirmBtnMode = ConfirmBtnModes.NONE

    private var mTodayTodo: Todo? = null

    private val mDb by lazy {
        Room.databaseBuilder(mView.getAppContext(), AppDatabase::class.java,
            AppDatabase.DATABASE_NAME).build()
    }

    private val mCalendar by lazy {
        Calendar.getInstance()
    }

    private val mAuthProvider = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    override fun loadMinYear() {
        mDb.todoDao()
            .getMinYear()
            .subscribeOn(Schedulers.io())
            .subscribe({
                minYear = it
            }, {
                minYear = mCalendar.getNowYear()
            })
    }

    override fun loadMaxYear() {
        mDb.todoDao()
            .getMaxYear()
            .subscribeOn(Schedulers.io())
            .subscribe({
                maxYear = it
            }, {
                maxYear = mCalendar.getNowYear()
            })
    }

    override fun loadTodos(year: Int, month: Int, setTodoOnView: (List<Todo>, Todo?) -> Unit) {
        mDb.todoDao()
            .getTodos(year, month)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val todayTodo = getTodayTodo(it)
                mTodayTodo = todayTodo
                val todayRemovedTodos = removeTodoFromList(it.toMutableList(), todayTodo)

                setTodoOnView(todayRemovedTodos, todayTodo)
            }, { it.printStackTrace() })
    }

    override fun addTodo(content: String, onTodoAdded: (Long) -> Unit) {
        val newTodo = Todo(0, content, mCalendar.getNowYear(), mCalendar.getNowMonth(),
            mCalendar.getNowDay(), false)
        mDb.todoDao().addNewTodo(newTodo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onTodoAdded, {
                it.printStackTrace()
            })
    }

    override fun updateTodo(toBeUpdatedTodo: Todo, onTodoUpdated: (Int) -> Unit) {
        mDb.todoDao().updateTodo(toBeUpdatedTodo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onTodoUpdated, {
                it.printStackTrace()
            })
    }

    override fun deleteTodo(toBeDeletedTodo: Todo, onTodoDeleted: (Int) -> Unit) {
        mDb.todoDao().removeTodo(toBeDeletedTodo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onTodoDeleted, {
                it.printStackTrace()
            })
    }

    override fun getNowYear() = mCalendar.getNowYear()
    override fun getNowMonth() = mCalendar.getNowMonth()
    override fun getNowDay() = mCalendar.getNowDay()

    override fun getMinYear() = if (minYear != AppDatabase.INVALID_YEAR) minYear else mCalendar.getNowYear()
    override fun getMaxYear() = if (maxYear != AppDatabase.INVALID_YEAR) maxYear else mCalendar.getNowYear()

    override fun makeTodayTodo() = Todo.makeTodayTodo(mCalendar)


    override fun isThisMonth(year: Int, month: Int): Boolean {
        return year == getNowYear() && month == getNowMonth()
    }

    override fun getTodoDAO(): TodoDAO {
        return mDb.todoDao()
    }

    override fun setConfirmBtnMode(@ConfirmBtnModes.ConfirmBtnMode confirmBtnMode: Int) {
        mConfirmBtnMode = confirmBtnMode
    }

    override fun getConfirmBtnMode(): Int {
        return mConfirmBtnMode
    }

    override fun getTodayTodo(): Todo? {
        return mTodayTodo
    }

    override fun getAuthProvider(): ArrayList<AuthUI.IdpConfig> {
        return mAuthProvider
    }

    private fun getTodayTodo(todos: List<Todo>): Todo? {
        if (todos.isEmpty()) {
            return null
        }

        for (todo in todos) {
            if (todo.isTodayTodo(mCalendar)) {
                return todo
            }
        }

        return null
    }

    private fun removeTodoFromList(todos: MutableList<Todo>, todo: Todo?): MutableList<Todo> {
        if (todo != null) {
            todos.remove(todo)
        }

        return todos
    }
}
package com.example.yebon.one_todo

import android.content.Context
import com.example.yebon.one_todo.db.TodoDAO
import com.example.yebon.one_todo.db.model.Todo

interface MainContract {

    interface View {
        fun getAppContext(): Context
        fun toastError()
    }

    interface Presenter {
        fun loadMinYear()
        fun loadMaxYear()
        fun loadTodos(year: Int, month: Int, setTodoOnView: (List<Todo>, Todo?) -> Unit)
        fun addTodo(content: String, onTodoAdded: (Long) -> Unit)
        fun updateTodo(toBeUpdatedTodo: Todo, onTodoUpdated: (Int) -> Unit)
        fun deleteTodo(toBeDeletedTodo: Todo, onTodoDeleted: (Int) -> Unit)
        fun getNowYear(): Int
        fun getNowMonth(): Int
        fun getNowDay(): Int
        fun getMinYear(): Int
        fun getMaxYear(): Int
        fun makeTodayTodo(): Todo
        fun isThisMonth(year: Int, month: Int): Boolean
        fun getTodoDAO(): TodoDAO
        fun setConfirmBtnMode(@ConfirmBtnModes.ConfirmBtnMode confirmBtnType: Int)
        @ConfirmBtnModes.ConfirmBtnMode
        fun getConfirmBtnMode(): Int
        fun getTodayTodo(): Todo?
    }
}
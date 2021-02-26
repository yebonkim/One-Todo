package com.example.yebon.one_todo.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.yebon.one_todo.R
import com.example.yebon.one_todo.db.TodoDAO
import com.example.yebon.one_todo.db.model.Todo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_editing.*

class EditDialog(context: Context,
                 private val todo: Todo,
                 private val todoDAO: TodoDAO) : Dialog(context), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_editing)

        setCancelable(true)

        todo_edit.setText(todo.contents)
        cancel.setOnClickListener(this)
        delete.setOnClickListener(this)
        complete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.cancel -> {
                dismiss()
            }
            R.id.delete -> {
                todoDAO.removeTodo(todo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Toast.makeText(context, R.string.todo_deleted, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }, {
                        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                    })
            }
            R.id.complete -> {
                todo.contents = todo_edit.text.toString()
                todoDAO.updateTodo(todo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Toast.makeText(context, R.string.todo_updated, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }, {
                        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                    })
            }
        }
    }
}
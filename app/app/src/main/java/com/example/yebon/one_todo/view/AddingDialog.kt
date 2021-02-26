package com.example.yebon.one_todo.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.room.EmptyResultSetException
import com.example.yebon.one_todo.R
import com.example.yebon.one_todo.db.TodoDAO
import com.example.yebon.one_todo.db.model.Todo
import com.example.yebon.one_todo.utils.DateUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_adding.*

class AddingDialog(
    context: Context,
    private val todoDAO: TodoDAO,
    private val year: Int,
    private val month: Int,
    private val day: Int,
    private val onAddedListener: (year: Int, month: Int)->Unit
) : Dialog(context), View.OnClickListener {

    private var selectedYear = year
    private var selectedMonth = month
    private var selectedDay = day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_adding)

        setCancelable(true)
        date.setOnClickListener(this)
        confirm.setOnClickListener(this)
    }

    private val onDateSetListener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            selectedYear = year
            selectedMonth = month + 1
            selectedDay = dayOfMonth
            date.text = DateUtil.getDateStr(selectedYear, selectedMonth, selectedDay)
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.date -> {
                DatePickerDialog(context, onDateSetListener, year, month - 1, day).show()
            }
            R.id.confirm -> {
                val content = todo_edit.text.toString()

                if (!TextUtils.isEmpty(content)) {
                    getTodo(selectedYear, selectedMonth, selectedDay, {
                        addTodo(content, selectedYear, selectedMonth, selectedDay)
                    }, {
                        Toast.makeText(context, R.string.already_existed_todo, Toast.LENGTH_SHORT).show()
                    })
                } else {
                    Toast.makeText(context, R.string.input_todo, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getTodo(year: Int, month: Int, day: Int, insert: () -> Unit, alertExisted: () -> Unit): Unit {
        todoDAO.getTodo(year, month, day)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if (it != null) {
                    alertExisted()
                }
            }, {
                if (it is EmptyResultSetException) {
                    insert()
                } else {
                    dismiss()
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun addTodo(content: String, year: Int, month: Int, day: Int) {
        todoDAO.addNewTodo(Todo(0, content, year, month, day, false))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                onAddedListener(month, day)
                dismiss()
            }, {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                dismiss()
            })
    }
}
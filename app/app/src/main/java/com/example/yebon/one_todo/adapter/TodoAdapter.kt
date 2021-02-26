package com.example.yebon.one_todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.yebon.one_todo.R
import com.example.yebon.one_todo.db.AppDatabase
import com.example.yebon.one_todo.db.model.Todo
import com.example.yebon.one_todo.utils.DateUtil
import com.example.yebon.one_todo.view.EditDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.viewholder_other_todo.view.*

class TodoAdapter(private val list: List<Todo>, val dismissListener: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val OTHER_TODO_VIEWHOLDER = 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            OTHER_TODO_VIEWHOLDER -> (holder as? OtherViewHolder)?.onBind(list[position])
        }
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        return OTHER_TODO_VIEWHOLDER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            OTHER_TODO_VIEWHOLDER -> OtherViewHolder(parent)
            else -> OtherViewHolder(parent)
        }
    }

    inner class OtherViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.viewholder_other_todo, parent, false)
    ) {
        private val db by lazy {
            Room.databaseBuilder(parent.context, AppDatabase::class.java,
                AppDatabase.DATABASE_NAME).build()
        }

        fun onBind(data: Todo) {
            itemView.onBind(data)
        }

        private fun View.onBind(data: Todo) {
            content.text = data.contents
            date.text = DateUtil.getDateStr(data.year, data.month, data.day)

            setRadioBackground(data.isDone)
            isDone.setOnClickListener {
                data.isDone = !data.isDone
                setRadioBackground(data.isDone)
                db.todoDao()
                    .updateTodo(data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it != 1) {
                            // when fail to update todo
                            data.isDone = !data.isDone
                            setRadioBackground(data.isDone)
                        }
                    }, { it.printStackTrace() })
            }
            itemView.setOnClickListener {
                val editDialog = EditDialog(context, data, db.todoDao())
                editDialog.setOnDismissListener {
                    dismissListener()
                }
                editDialog.show()
            }
        }

        private fun View.setRadioBackground(isTodoDone: Boolean) {
            if (isTodoDone) {
                isDone.background = resources.getDrawable(R.drawable.ic_radio_selected, null)
            } else {
                isDone.background = resources.getDrawable(R.drawable.ic_radio_unselected, null)
            }
        }
    }
}
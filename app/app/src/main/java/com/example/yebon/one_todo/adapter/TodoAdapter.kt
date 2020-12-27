package com.example.yebon.one_todo.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yebon.one_todo.adapter.holder.OtherViewHolder
import com.example.yebon.one_todo.adapter.holder.AddTodayViewHolder
import com.example.yebon.one_todo.adapter.holder.TodayViewHolder
import com.example.yebon.one_todo.db.TodoDAO
import com.example.yebon.one_todo.db.model.Todo

class TodoAdapter(private val list: List<Todo>, private val todoDAO : TodoDAO) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ADD_TODAY_TODO_VIEWHOLDER = 1
    private val TODAY_TODO_VIEWHOLDER = 2
    private val OTHER_TODO_VIEWHOLDER = 3

    private val isTodayToDoExist = false

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ADD_TODAY_TODO_VIEWHOLDER -> (holder as? AddTodayViewHolder)?.onBind(list[position])
            TODAY_TODO_VIEWHOLDER -> (holder as? TodayViewHolder)?.onBind(list[position])
            OTHER_TODO_VIEWHOLDER -> (holder as? OtherViewHolder)?.onBind(list[position])
        }
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> if (isTodayToDoExist) TODAY_TODO_VIEWHOLDER else ADD_TODAY_TODO_VIEWHOLDER
            else -> OTHER_TODO_VIEWHOLDER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADD_TODAY_TODO_VIEWHOLDER -> AddTodayViewHolder(parent)
            TODAY_TODO_VIEWHOLDER -> TodayViewHolder(parent)
            OTHER_TODO_VIEWHOLDER -> OtherViewHolder(parent)
            else -> OtherViewHolder(parent)
        }
    }
}
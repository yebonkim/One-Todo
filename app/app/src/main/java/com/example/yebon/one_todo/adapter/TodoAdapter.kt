package com.example.yebon.one_todo.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yebon.one_todo.adapter.holder.OtherViewHolder
import com.example.yebon.one_todo.db.model.Todo

class TodoAdapter(private val list: List<Todo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
}
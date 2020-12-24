package com.example.yebon.one_todo.adapter.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yebon.one_todo.R
import com.example.yebon.one_todo.db.model.Todo
import kotlinx.android.synthetic.main.viewholder_today_todo.view.*

class OtherViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.viewholder_today_todo, parent, false)
) {
    fun onBind(data: Todo) {
        itemView.onBind(data)
    }

    private fun View.onBind(data: Todo) {
        todo_edit.setText(data.contents)
    }
}
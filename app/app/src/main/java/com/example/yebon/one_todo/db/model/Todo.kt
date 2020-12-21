package com.example.yebon.one_todo.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val contents: String,
    val year: Int,
    val month: Int,
    val isDone: Boolean
)